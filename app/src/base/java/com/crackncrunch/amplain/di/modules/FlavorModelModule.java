package com.crackncrunch.amplain.di.modules;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.crackncrunch.amplain.App;
import com.crackncrunch.amplain.utils.AppConfig;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Lilian on 26-Feb-17.
 */

@Module
public class FlavorModelModule {
    @Provides
    JobManager provideJobManager() {
        Configuration configuration = new Configuration.Builder(App.getContext())
                .minConsumerCount(AppConfig.MIN_CONSUMER_COUNT) // always keep at least one consumer alive
                .maxConsumerCount(AppConfig.MAX_CONSUMER_COUNT) // up to 3 consumers at a time
                .loadFactor(AppConfig.LOAD_FACTOR) // 3 jobs per consumer
                .consumerKeepAlive(AppConfig.KEEP_ALIVE) // keep alive 2 min if thread has no consumer
                .build();
        return new JobManager(configuration);
    }
}
