package com.crackncrunch.amplain.di.modules;

import android.content.Context;

import com.crackncrunch.amplain.di.scopes.RootScope;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Lilian on 21-Feb-17.
 */

@Module
public class PicassoCacheModule {

    @Provides
    @RootScope
    Picasso providePicasso(Context context) {
        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(context);
        Picasso picasso = new Picasso.Builder(context)
                .downloader(okHttp3Downloader)
                .debugging(true)
                .build();
        Picasso.setSingletonInstance(picasso);
        return picasso;
    }
}
