package mn.devfest;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Release DevFest Dagger component
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