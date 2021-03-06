package br.com.raphasil.vertx.sample;

import java.util.Random;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

import br.com.raphasil.vertx.sample.infrastructure.VertxFactory;
import br.com.raphasil.vertx.sample.infrastructure.constant.ConfigJsonConstant;
import br.com.raphasil.vertx.sample.infrastructure.helper.ResourceHelper;
import br.com.raphasil.vertx.sample.infrastructure.helper.VertxDeployHelper;
import io.reactivex.functions.Consumer;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class VertxMain implements Daemon {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(VertxMain.class);

	private final ResourceHelper resourceHelper;
	private final VertxDeployHelper vertxDeployHelper;
	private final Consumer<String> logSuccess;
	private final Consumer<Throwable> logError;

	public VertxMain() {
		this(new ResourceHelper(), new VertxDeployHelper(), message -> LOGGER.info("Verticle Deployed {0}", message), throwable -> LOGGER.error("Verticle Could not be Deployed {0}", throwable));
	}
	
	public VertxMain(ResourceHelper resourceHelper, VertxDeployHelper vertxDeployHelper, Consumer<String> logSuccess, Consumer<Throwable> logError) {
		this.resourceHelper = resourceHelper;
		this.vertxDeployHelper = vertxDeployHelper;
		this.logSuccess = logSuccess;
		this.logError = logError;
	}

	@Override
	public void init(DaemonContext context) throws DaemonInitException, Exception {
		init();
	}

	@Override
	public void start() throws Exception {

	}

	@Override
	public void stop() throws Exception {
		
	}

	@Override
	public void destroy() {

	}
	
	public void init() {
		Random rand = new Random();
		int id = rand.nextInt(30);
		
		System.setProperty("server.name", "server " + id);
		
		resourceHelper.getConfig()
				.ifPresent(c -> VertxFactory.create(new VertxOptions(c.getJsonObject(ConfigJsonConstant.VERTX_OPTIONS)))
				.flatMapObservable(vertx -> vertxDeployHelper.deployVerticles(vertx, c.getJsonObject(ConfigJsonConstant.LIST_VERTICLES)))
				.subscribe(logSuccess, logError));
	}

	public static void main(String[] args) throws Exception {
		new VertxMain().init();
	}
}
