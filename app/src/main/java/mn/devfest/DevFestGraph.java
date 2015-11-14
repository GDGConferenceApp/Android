package mn.devfest;

import mn.devfest.api.DevFestApi;

/**
 * Common interface for variant-specific dependencies
 *
 * @author bherbst
 */
public interface DevFestGraph {
    DevFestApi devFestApi();
}
