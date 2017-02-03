package com.devfestmn;

import javax.inject.Singleton;

import dagger.Component;
import com.devfestmn.api.ReleaseApiModule;
import com.devfestmn.data.DataModule;

/**
 * Release DevFest Dagger component
 *
 * @author bherbst
 */
@Singleton
@Component(modules = {
        ReleaseApiModule.class,
        DataModule.class
})
public interface DevFestComponent extends DevFestGraph {
    final class Initializer {
        static DevFestGraph init(DevFestApplication application) {
            return DaggerDevFestComponent.builder()
                    .dataModule(new DataModule(application))
                    .build();
        }

        private Initializer() {
            // Block instantiation
        }
    }
}