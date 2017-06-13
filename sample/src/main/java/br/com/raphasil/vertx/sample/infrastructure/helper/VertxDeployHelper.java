package br.com.raphasil.vertx.sample.infrastructure.helper;

import java.util.Map;

import br.com.raphasil.vertx.sample.infrastructure.constant.ConfigJsonConstant;
import io.reactivex.Observable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class VertxDeployHelper {
	
	public Observable<String> deployVerticles(Vertx vertx, JsonObject verticles) {
		return Observable.fromIterable(verticles).flatMap(conf -> deployVerticle(vertx, conf));
	}

	private Observable<String> deployVerticle(Vertx vertx, Map.Entry<String, Object> conf) {
		JsonObject json = ((JsonObject) conf.getValue());
		JsonObject config = json.getJsonObject(ConfigJsonConstant.DEPLOY_OPTIONS);
		DeploymentOptions options = new DeploymentOptions(config);		
				
		return Observable.fromPublisher(observer -> {
			vertx.deployVerticle(conf.getKey(), options, result -> {
				if (result.succeeded()) {
					observer.onNext(result.result());
				} else {
					observer.onError(result.cause());
				}
				observer.onComplete();
			});
		});
	}

}
