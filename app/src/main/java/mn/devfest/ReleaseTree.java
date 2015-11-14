package mn.devfest;


import timber.log.Timber;

/**
 * Timber tree used for releases.
 *
 * Currently logs nothing to logcat.
 *
 * @author bherbst
 */
class ReleaseTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        // Log nothing
    }
}
