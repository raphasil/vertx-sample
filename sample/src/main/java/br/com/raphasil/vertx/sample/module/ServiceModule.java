package br.com.raphasil.vertx.sample.module;

import com.google.inject.AbstractModule;

import br.com.raphasil.vertx.sample.service.SampleService;

public class ServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		
		bind(SampleService.class);
		
	}

}
