package com.crackncrunch.amplain.di.components;

import com.crackncrunch.amplain.di.modules.ModelModule;
import com.crackncrunch.amplain.mvp.models.AbstractModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Lilian on 21-Feb-17.
 */

@Component(modules = ModelModule.class)
@Singleton
public interface ModelComponent {
    void inject(AbstractModel target);
}
