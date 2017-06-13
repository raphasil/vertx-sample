package br.com.raphasil.vertx.sample.verticle;

import java.util.concurrent.TimeUnit;

import com.google.inject.Guice;
import com.google.inject.Inject;

import br.com.raphasil.vertx.sample.controller.SampleEventController;
import br.com.raphasil.vertx.sample.module.ControllerModule;
import br.com.raphasil.vertx.sample.module.MainModule;
import io.reactivex.Scheduler.Worker;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.dropwizard.MetricsService;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;



public class ControllerVerticle extends AbstractVerticle {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ControllerVerticle.class);
	
	private Worker worker;
	
	private HttpServer httpServer;
	
	private MetricsService metricsService;
	
	@Inject
	private SampleEventController sampleController;
	
	@Inject
	private EventBus eventBus;
	
	@Override
    public void start(Future<Void> fut) {
				
		Guice.createInjector(new MainModule(vertx), new ControllerModule()).injectMembers(this);
		
		Router router = Router.router(vertx);
		
		router.route().handler(BodyHandler.create());
		
		router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.DELETE)
                .allowedHeader("user")
                .allowedHeader("token")
                .allowedHeader("Access-Control-Request-Method")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Headers")
                .allowedHeader("Content-Type"));
		
		sampleController.config(router);
		
		createSocketBridge(router);
		
		router.route("/*").handler(StaticHandler.create("webroot"));
		
		final Integer port = config().getInteger("http.port", 9090);
		
		httpServer = vertx.createHttpServer().requestHandler(router::accept).listen(port, result -> {
			if(result.succeeded()) {
				LOGGER.info("[{0}] - ControllerVerticle start, port {1}", Thread.currentThread().getId(), port);
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		});
		
		metricsService = MetricsService.create(vertx);
		
		worker = Schedulers.newThread().createWorker();
		
		startMetricsEvents();
    }	
	
	private void createSocketBridge(Router router) {
		
		BridgeOptions options = new BridgeOptions().
		        addOutboundPermitted(
		            new PermittedOptions().
		                setAddress("metrics")
		        );
		
		SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
	    sockJSHandler.bridge(options);
	    router.route("/eventbus/*").handler(sockJSHandler);
	}
	
	private void startMetricsEvents() {
		
		worker.schedulePeriodically(new Runnable() {
			
			@Override
			public void run() {
				// LOGGER.info("metrics eventbus");
				JsonObject metrics = metricsService.getMetricsSnapshot(eventBus);				
				eventBus.publish("metrics", metrics);
			}
		}, 100, 100, TimeUnit.MILLISECONDS);
	}
    
    @Override
    public void stop() throws Exception {
    	worker.dispose();
    	httpServer.close();
    	
    	LOGGER.info("[{0}] - ControllerVerticle stop", Thread.currentThread().getId());      
    }

}
