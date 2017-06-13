package br.com.raphasil.vertx.sample.verticle;

import com.google.inject.Guice;
import com.google.inject.Inject;

import br.com.raphasil.vertx.sample.client.RandomUserRequest;
import br.com.raphasil.vertx.sample.module.MainModule;
import br.com.raphasil.vertx.sample.module.RandomUserModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ClientRandomUserVerticle extends AbstractVerticle {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ClientRandomUserVerticle.class);
	
	@Inject
	private RandomUserRequest randomUserRequest;
	
	@Override
	public void start() throws Exception {
		Guice.createInjector(new MainModule(vertx), new RandomUserModule(vertx)).injectMembers(this);
		
		randomUserRequest.config();
		
		LOGGER.info("[{0}] ClientRandomUserVerticle start", Thread.currentThread().getId());
	}
	
	@Override
	public void stop() throws Exception {
		LOGGER.info("[{0}] ClientRandomUserVerticle stop", Thread.currentThread().getId());		
	}

}
