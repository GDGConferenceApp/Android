package mn.devfest;

import mn.devfest.api.DevFestApi;
import mn.devfest.api.DevFestDataSource;

/**
 * Common interface for variant-specific dependencies
 *
 * @author bherbst
 */
public interface DevFestGraph {
    DevFestApi devFestApi();

    DevFestDataSource datasource();
}
