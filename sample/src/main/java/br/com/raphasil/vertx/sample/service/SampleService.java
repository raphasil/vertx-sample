package br.com.raphasil.vertx.sample.service;

import com.google.inject.Inject;

import br.com.raphasil.vertx.sample.infrastructure.constant.EventConstant;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

public class SampleService {
	
	private final EventBus eventBus;
	
	@Inject
	public SampleService(EventBus eventBus) {
		this.eventBus = eventBus;
	}
		
	private void getSample(Message<String> message) {
		String id = Thread.currentThread().getId() + "";
		System.out.println(String.format("[%s] Sample Service, recive: %s", id, message.body()));
		
		String reply = String.format("[%s] - get sample service", id);
		
		message.reply(reply);
	}

	public void config() {
		eventBus.consumer(EventConstant.SERVICE_SAMPLE_GET_SAMPLE, this::getSample);		
	}

}
