package br.com.raphasil.vertx.sample.module;

import com.google.inject.AbstractModule;

import br.com.raphasil.vertx.sample.client.RandomUserRequest;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;

public class RandomUserModule extends AbstractModule {
	
	private final Vertx vertx;    

	public RandomUserModule(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	protected void configure() {
		
		bind(HttpClient.class).toInstance(vertx.createHttpClient());
		
		bind(RandomUserRequest.class);
	}

}
