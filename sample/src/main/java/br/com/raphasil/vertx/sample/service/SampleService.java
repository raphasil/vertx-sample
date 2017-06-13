package br.com.raphasil.vertx.sample.service;

import java.util.Date;

import com.google.inject.Inject;

import br.com.raphasil.vertx.sample.dto.MessageDTO;
import br.com.raphasil.vertx.sample.dto.StepDTO;
import br.com.raphasil.vertx.sample.infrastructure.constant.EventConstant;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;

public class SampleService {
	
	private final EventBus eventBus;
	
	@Inject
	public SampleService(EventBus eventBus) {
		this.eventBus = eventBus;
	}
		
	private void getSample(Message<String> message) {
		MessageDTO body = Json.decodeValue(message.body(), MessageDTO.class);
		
		body.getSteps().add(createStep("SampleService::getSample"));
		
		message.reply(Json.encode(body));
	}
	
	private void getUser(Message<String> message) {
		
		eventBus.send(EventConstant.RANDOM_USER_REQUEST_GET_USER, message.body(), reply -> {
			if(reply.succeeded()) {
				message.reply(reply.result().body());				
			} else {				
				message.fail(500, reply.cause().getMessage());
			}
		});
		
	}
	
	private void getUserParallel(Message<String> message) {
		MessageDTO body = Json.decodeValue(message.body(), MessageDTO.class);
		
		body.getSteps().add(createStep("SampleService::getUserParallel::request"));
		
		eventBus.send(EventConstant.RANDOM_USER_REQUEST_GET_USER_PARALLEL, Json.encode(body), reply -> {
			if(reply.succeeded()) {
				MessageDTO result = Json.decodeValue(reply.result().body().toString(), MessageDTO.class);
				result.getSteps().add(createStep("SampleService::getUserParallel::response"));
				message.reply(Json.encode(result));
			} else {				
				message.fail(500, reply.cause().getMessage());
			}
		});
		
	}
	
	private StepDTO createStep(String message) {
		final StepDTO step = new StepDTO();
		step.setId(Thread.currentThread().getId());
		step.setMessage(message);
		step.setDate(new Date());
		return step;
	}

	public void config() {
		eventBus.consumer(EventConstant.SERVICE_SAMPLE_GET_SAMPLE, this::getSample);
		eventBus.consumer(EventConstant.SERVICE_SAMPLE_GET_USER, this::getUser);
		eventBus.consumer(EventConstant.SERVICE_SAMPLE_GET_USER_PARALLEL, this::getUserParallel);
	}

}
