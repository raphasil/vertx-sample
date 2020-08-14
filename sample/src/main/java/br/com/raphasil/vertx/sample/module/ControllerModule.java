package br.com.raphasil.vertx.sample.module;

import com.google.inject.AbstractModule;

import br.com.raphasil.vertx.sample.controller.SampleEventController;

public class ControllerModule extends AbstractModule {

	@Override
	protected void configure() {
		
		bind(SampleEventController.class);
		
	}

}
