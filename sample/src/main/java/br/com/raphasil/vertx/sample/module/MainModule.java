package br.com.raphasil.vertx.sample.module;

import java.io.IOException;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class MainModule extends AbstractModule {
	
	private final Vertx vertx;    

	public MainModule(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	protected void configure() {
		
		bind(EventBus.class).toInstance(vertx.eventBus());
		
		bind(Context.class).toInstance(vertx.getOrCreateContext());
		
		Names.bindProperties(binder(), buildProperties());		
	}

	private Properties buildProperties() {
		Properties properties = new Properties();
		
		try {
			properties.load(getClass().getResourceAsStream("/application.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        return properties;
	}

}
