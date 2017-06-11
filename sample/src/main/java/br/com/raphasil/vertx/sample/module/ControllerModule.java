package br.com.raphasil.vertx.sample.module;

import com.google.inject.AbstractModule;

import br.com.raphasil.vertx.sample.controller.SampleController;

public class ControllerModule extends AbstractModule {

	@Override
	protected void configure() {
		
		bind(SampleController.class);
		
	}

}
