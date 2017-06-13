package br.com.raphasil.vertx.sample.infrastructure;

import java.util.concurrent.TimeUnit;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.LifecycleEvent;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class ClusteredVertxFactory implements VertxFactory {

	private ICountDownLatch latch;

	@Override
	public Single<Vertx> createVertx(VertxOptions vertxOptions) {
		HazelcastClusterManager clusterManager = getClusterManager();
		this.latch = clusterManager.getHazelcastInstance().getCountDownLatch("shutdown.latch");
		latch.trySetCount(1);

		vertxOptions.setClusterManager(clusterManager);
		return Single.fromPublisher(publisher -> {
			Vertx.clusteredVertx(vertxOptions, response -> {
				if (response.succeeded()) {
					Vertx vertx = response.result();
					clusterManager.getHazelcastInstance().getLifecycleService().addLifecycleListener(state -> {
						if (state.getState() == LifecycleEvent.LifecycleState.SHUTTING_DOWN) {
							beforeLeaveUndeploy(vertx);
						}
					});
					publisher.onNext(vertx);
				} else {
					publisher.onError(response.cause());
				}

				publisher.onComplete();
			});
		});
	}

	private HazelcastClusterManager getClusterManager() {
		HazelcastInstance instance = Hazelcast.newHazelcastInstance();
		return new HazelcastClusterManager(instance);
	}

	public void beforeLeaveUndeploy(Vertx vertx) {
		
		Observable.fromIterable(vertx.deploymentIDs()).flatMapCompletable(id -> Completable.fromSingle(s -> {
			vertx.undeploy(id, s::onSuccess);
		})).doOnComplete(latch::countDown).subscribe();
		
		try {
			latch.await(30000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
