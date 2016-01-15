package mn.devfest;

import javax.inject.Singleton;

import dagger.Component;
import mn.devfest.api.DebugApiModule;
import mn.devfest.data.DataModule;

/**
 * Debug DevFest Dagger component
 *
 * @author bherbst
 */
@Singleton
@Component(modules = {
        DebugApiModule.class,
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