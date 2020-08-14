package br.com.raphasil.vertx.sample.infrastructure;

import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public interface VertxFactory {
	
	static Single<Vertx> create(VertxOptions vertxOptions) {
        if (vertxOptions.isClustered()) {
            return new ClusteredVertxFactory().createVertx(vertxOptions);
        } else {
            return new StandaloneVertxFactory().createVertx(vertxOptions);
        }
    }

    Single<Vertx> createVertx(VertxOptions vertxOptions);

    void beforeLeaveUndeploy(Vertx vertx);

}
