package mn.devfest;

import mn.devfest.api.DevFestApi;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.api.FeedbackApi;

/**
 * Common interface for variant-specific dependencies
 *
 * @author bherbst
 */
public interface DevFestGraph {
    DevFestApi devFestApi();

    FeedbackApi feedbackApi();

    DevFestDataSource datasource();
}
