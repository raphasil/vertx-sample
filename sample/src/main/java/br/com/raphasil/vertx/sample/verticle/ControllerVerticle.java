package br.com.raphasil.vertx.sample.verticle;

import com.google.inject.Guice;
import com.google.inject.Inject;

import br.com.raphasil.vertx.sample.controller.SampleController;
import br.com.raphasil.vertx.sample.module.ControllerModule;
import br.com.raphasil.vertx.sample.module.MainModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

public class ControllerVerticle extends AbstractVerticle {
	
	@Inject
	private SampleController sampleController; 
	
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
		
		Integer port = config().getInteger("http.port", 9090);
		
		vertx.createHttpServer().requestHandler(router::accept).listen(port, result -> {
			if(result.succeeded()) {
				System.out.println(String.format("[%s] - ControllerVerticle start, port %s", Thread.currentThread().getId(), port));
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		});
    }
	
	
//	private void createSocketBridge(Router router) {
//		SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
//	    sockJSHandler.bridge(new BridgeOptions());
//	    router.route("/eventbus/*").handler(sockJSHandler);
//	}
    
    @Override
    public void stop() throws Exception {
    	System.out.println(String.format("[%s] - ControllerVerticle stop", Thread.currentThread().getId()));      
    }

}
