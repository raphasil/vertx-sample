package br.com.raphasil.vertx.sample.verticle;

import com.google.inject.Guice;
import com.google.inject.Inject;

import br.com.raphasil.vertx.sample.module.MainModule;
import br.com.raphasil.vertx.sample.module.ServiceModule;
import br.com.raphasil.vertx.sample.service.SampleService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ServiceVerticle extends AbstractVerticle {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ServiceVerticle.class);
			
	@Inject
	private SampleService sampleService;
	
	@Override
    public void start() {
		
		Guice.createInjector(new MainModule(vertx), new ServiceModule()).injectMembers(this);
		
		sampleService.config();
		
		LOGGER.info("[{0}] - ServiceVerticle start", Thread.currentThread().getId());
    }
    
    @Override
    public void stop() throws Exception {
    	LOGGER.info("[{0}] - ServiceVerticle stop", Thread.currentThread().getId());      
    }
}
