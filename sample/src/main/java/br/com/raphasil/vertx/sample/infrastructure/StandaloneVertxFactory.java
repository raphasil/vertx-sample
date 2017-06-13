package br.com.raphasil.vertx.sample.infrastructure;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;

public class StandaloneVertxFactory implements VertxFactory {

	private CountDownLatch latch;

    @Override
    public Single<Vertx> createVertx(VertxOptions vertxOptions) {    	
        this.latch = new CountDownLatch(1);
        
        vertxOptions.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
        
        Vertx vertx = Vertx.vertx(vertxOptions);
        Single<Vertx> map = Single.just(vertx)
                     .map(v -> {                    	 
                         Runtime.getRuntime().addShutdownHook(new Thread(() -> beforeLeaveUndeploy(vertx)));
                         return v;
                     });
        
        return map;
    }

    public void beforeLeaveUndeploy(Vertx vertx) {
        Observable.fromIterable(vertx.deploymentIDs())
                  .flatMapCompletable(id -> Completable.fromSingle(s -> {
                                          vertx.undeploy(id, s::onSuccess);
                                      })
                                     )
                  .doOnComplete(latch::countDown)
                  .subscribe();
        try {
            latch.await(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
