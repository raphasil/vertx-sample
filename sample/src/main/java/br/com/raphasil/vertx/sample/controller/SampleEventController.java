package br.com.raphasil.vertx.sample.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;

import br.com.raphasil.vertx.sample.dto.MessageDTO;
import br.com.raphasil.vertx.sample.dto.StepDTO;
import br.com.raphasil.vertx.sample.infrastructure.constant.EventConstant;
import io.reactivex.Observable;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class SampleEventController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SampleEventController.class);
		
	private final EventBus eventBus;
	
	@Inject
	public SampleEventController(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	private StepDTO createStep(String message) {
		final StepDTO step = new StepDTO();
		step.setId(Thread.currentThread().getId());
		step.setMessage(message);
		step.setDate(new Date());
		return step;
	}
	
	private MessageDTO createMessage(String messageStep) {
		final List<StepDTO> steps = new LinkedList<>();
		steps.add(createStep(messageStep));
		
		final MessageDTO message = new MessageDTO();
		message.setStart(new Date());
		message.setSteps(steps);
		
		return message;
	}
	
	private void getSampleEventSend(RoutingContext rc) {
		
		final MessageDTO message = createMessage("SampleEventController::getSampleEventSend::http::request");
		
		eventBus.send(EventConstant.SERVICE_SAMPLE_GET_SAMPLE, Json.encode(message), reply -> {
			if(reply.succeeded()) {
				Message<Object> result = reply.result();
				MessageDTO body = Json.decodeValue(result.body().toString(), MessageDTO.class);
				body.getSteps().add(createStep("SampleEventController::getSampleEventSend::http::response"));
				body.setFinish(new Date());
				rc.response().putHeader("content-type", "application/json").setStatusCode(200).end(Json.encode(body));
			} else {
				rc.response().putHeader("content-type", "application/json").setStatusCode(500).end(Json.encode(reply.cause()));
			}
		});						
	}
	
	private void getSampleEventPublish(RoutingContext rc) {
		
		final MessageDTO message = createMessage("SampleEventController::getSampleEventPublish::http::request"); 
		
		eventBus.publish(EventConstant.SERVICE_SAMPLE_GET_SAMPLE, Json.encode(message));	
		
		message.setFinish(new Date());
		
		rc.response().setStatusCode(200).end(Json.encode(message));		
	}
	
	private void getSampleUser(RoutingContext rc) {
		
		eventBus.send(EventConstant.SERVICE_SAMPLE_GET_USER, "get user", reply -> {
			if(reply.succeeded()) {
				String body = reply.result().body().toString();
				rc.response().putHeader("content-type", "application/json").setStatusCode(200).end(body);
			} else {
				rc.response().putHeader("content-type", "application/json").setStatusCode(500).end(Json.encode(reply.cause()));
			}
		});
		
	}
	
	private void getSampleUserParallel(RoutingContext rc) {
		
		rc.response().putHeader("content-type", "application/json");
		
		Integer size = Integer.parseInt(rc.request().getParam("size"));
		
		Observable<Integer> nums = Observable.range(1,size);
		nums.flatMap( n -> parallel(n) )		
		.toList()		
		.subscribe(
				list -> {
					rc.response().setStatusCode(200).end(Json.encode(list)); 
				},				
				err -> { 
					rc.response().setStatusCode(500).end(Json.encode(err)); 
				}
		);
	}
	
	private Observable<MessageDTO> parallel(int num) {
		return Observable.create(s -> {
			
			final MessageDTO message = createMessage("SampleEventController::parallel::" + num);
			
			eventBus.send(EventConstant.SERVICE_SAMPLE_GET_USER_PARALLEL, Json.encode(message), reply -> {
				if(reply.succeeded()) {
					Message<Object> result = reply.result();
					MessageDTO body = Json.decodeValue(result.body().toString(), MessageDTO.class);
					body.getSteps().add(createStep("SampleEventController::parallel::" + num));
					body.setFinish(new Date());
					s.onNext(body);
					s.onComplete();
				} else {					
					s.onError(reply.cause());
				}
			});
			
		});
	}

	public void config(Router router) {
		router.get("/api/sample-event/send").produces("application/json").handler(this::getSampleEventSend);		
		router.get("/api/sample-event/publish").produces("application/json").handler(this::getSampleEventPublish);
		router.get("/api/sample/user").produces("application/json").handler(this::getSampleUser);
		router.get("/api/sample/user/parallel/:size").produces("application/json").handler(this::getSampleUserParallel);
	}

}
