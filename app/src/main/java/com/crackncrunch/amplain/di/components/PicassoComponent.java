package com.crackncrunch.amplain.di.components;

import com.crackncrunch.amplain.di.modules.PicassoCacheModule;
import com.crackncrunch.amplain.di.scopes.RootScope;
import com.squareup.picasso.Picasso;

import dagger.Component;

/**
 * Created by Lilian on 21-Feb-17.
 */

@Component(dependencies = AppComponent.class,
        modules = PicassoCacheModule.class)
@RootScope
public interface PicassoComponent {
    Picasso getPicasso();
}
