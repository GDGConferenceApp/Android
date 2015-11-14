package mn.devfest;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Debug DevFest Dagger component
 *
 * @author bherbst
 */
@Singleton
@Component()
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