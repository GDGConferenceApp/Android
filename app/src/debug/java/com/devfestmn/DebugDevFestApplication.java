package com.devfestmn;

import com.facebook.stetho.Stetho;

import timber.log.Timber;

/**
 * Debug DevFest application class
 *
 * Performs application-wide configuration for debug builds
 *
 * @author bherbst
 */
public class DebugDevFestApplication extends DevFestApplication {

    @Override
    protected void init() {
        initStetho();
        Timber.plant(new Timber.DebugTree());
    }

    /**
     * Setup Stetho for debugging
     */
    private void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build()
        );
    }
}
