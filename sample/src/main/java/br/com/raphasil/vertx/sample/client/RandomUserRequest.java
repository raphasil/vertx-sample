package br.com.raphasil.vertx.sample.client;

import java.util.Date;

import com.google.inject.Inject;

import br.com.raphasil.vertx.sample.dto.MessageDTO;
import br.com.raphasil.vertx.sample.dto.StepDTO;
import br.com.raphasil.vertx.sample.infrastructure.constant.EventConstant;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class RandomUserRequest {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RandomUserRequest.class);
	
	private final HttpClient client;
	private final EventBus eventBus;
	
	@Inject
	public RandomUserRequest(HttpClient client, EventBus eventBus) {
		this.client = client;
		this.eventBus = eventBus;
	}
	
	private void getUser(Message<String> message) {
		
		
		HttpClientRequest request = client.getAbs("https://randomuser.me/api", response -> {
			if(response.statusCode() == 200) {
				response.bodyHandler(buffer -> {
					int length = buffer.length();
					String json = buffer.getString(0, length -1);
					message.reply(json);
				});
			} else {
				message.fail(response.statusCode(), response.statusMessage());
			}
		});
		
		request.putHeader("content-type", "application/json");
		request.end();
	}
	
	private void getUserParallel(Message<String> message) {
		
		MessageDTO body = Json.decodeValue(message.body(), MessageDTO.class);
		
		body.getSteps().add(createStep("RandomUserRequest::getUserParallel::request"));
		
		HttpClientRequest request = client.getAbs("https://randomuser.me/api", response -> {
			if(response.statusCode() == 200) {
				response.bodyHandler(buffer -> {
					int length = buffer.length();
					String json = buffer.getString(0, length);
					
					JsonObject jsonObject = new JsonObject(json);
					String name = jsonObject.getJsonArray("results").getJsonObject(0).getJsonObject("name").getString("first");
					
					body.getSteps().add(createStep("RandomUserRequest::getUserParallel::response"));
					body.setResult(name);
					String result = Json.encode(body);
					
					message.reply(result);
				});
			} else {
				message.fail(response.statusCode(), response.statusMessage());
			}
		});
		
		request.putHeader("content-type", "application/json");
		request.end();
	}
	
	private StepDTO createStep(String message) {
		final StepDTO step = new StepDTO();
		step.setId(Thread.currentThread().getId());
		step.setMessage(message);
		step.setDate(new Date());
		return step;
	}

	public void config() {
		eventBus.consumer(EventConstant.RANDOM_USER_REQUEST_GET_USER, this::getUser);
		eventBus.consumer(EventConstant.RANDOM_USER_REQUEST_GET_USER_PARALLEL, this::getUserParallel);
	}

}
