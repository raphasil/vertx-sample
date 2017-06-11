package br.com.raphasil.vertx.sample.controller;

import javax.annotation.PostConstruct;

import com.google.inject.Inject;

import br.com.raphasil.vertx.sample.infrastructure.constant.EventConstant;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class SampleController {
		
	private final EventBus eventBus;
	
	@Inject
	public SampleController(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	@PostConstruct
	private void load() {
		System.out.println("post contruct GetSampleController");
	}
	
	private void getSampleEventSend(RoutingContext rc) {
		String id = Thread.currentThread().getId() + "";
		String message = String.format("[%s] - get sample controller", id); 
		
		eventBus.send(EventConstant.SERVICE_SAMPLE_GET_SAMPLE, message, reply -> {
			if(reply.succeeded()) {
				Message<Object> result = reply.result();
				String body = (String) result.body();
				String response = String.format("send: %s receive: %s", message, body);
				rc.response().setStatusCode(200).end(response);
			} else {
				rc.response().setStatusCode(500).end("error: " + reply.cause().getMessage());
			}
		});						
	}
	
	private void getSampleEventPublish(RoutingContext rc) {
		String id = Thread.currentThread().getId() + "";
		String message = String.format("[%s] - get sample controller", id); 
		
		eventBus.publish(EventConstant.SERVICE_SAMPLE_GET_SAMPLE, message);	
		
		rc.response().setStatusCode(200).end(message);
	}

	public void config(Router router) {
		router.get("/sample-event-send").handler(this::getSampleEventSend);
		router.get("/sample-event-publish").handler(this::getSampleEventPublish);
	}

}
