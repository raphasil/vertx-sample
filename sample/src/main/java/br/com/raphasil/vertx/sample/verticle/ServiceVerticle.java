package br.com.raphasil.vertx.sample.verticle;

import com.google.inject.Guice;
import com.google.inject.Inject;

import br.com.raphasil.vertx.sample.module.MainModule;
import br.com.raphasil.vertx.sample.module.ServiceModule;
import br.com.raphasil.vertx.sample.service.SampleService;
import io.vertx.core.AbstractVerticle;

public class ServiceVerticle extends AbstractVerticle {
			
	@Inject
	private SampleService sampleService;
	
	@Override
    public void start() {
		
		Guice.createInjector(new MainModule(vertx), new ServiceModule()).injectMembers(this);
		
		sampleService.config();
				
		System.out.println(String.format("[%s] - ServiceVerticle start", Thread.currentThread().getId()));		        
    }
    
    @Override
    public void stop() throws Exception {
      
    }

}
