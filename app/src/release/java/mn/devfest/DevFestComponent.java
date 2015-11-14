package mn.devfest;

import javax.inject.Singleton;

import dagger.Component;
import mn.devfest.api.ReleaseApiModule;

/**
 * Release DevFest Dagger component
 *
 * @author bherbst
 */
@Singleton
@Component(modules = ReleaseApiModule.class)
public interface DevFestComponent extends DevFestGraph {
    final class Initializer {
        static DevFestGraph init() {
            return DaggerDevFestComponent.create();
        }

        private Initializer() {
            // Block instantiation
        }
    }
}