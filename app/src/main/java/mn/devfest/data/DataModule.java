package mn.devfest.data;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mn.devfest.DevFestApplication;
import mn.devfest.api.DevFestApi;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.schedule.UserScheduleRepository;

/**
 * Provides data-layer dependencies
 *
 * @author bherbst
 */
@Module
public class DataModule {
    private final DevFestApplication mApplication;

    public DataModule(DevFestApplication mApplication) {
        this.mApplication = mApplication;
    }

    @Provides
    @Singleton
    public UserScheduleRepository provideUserScheduleRepository() {
        return new UserScheduleRepository(mApplication);
    }

    @Provides
    @Singleton
    public DevFestDataSource provideDevFestDataSource(DevFestApi api, UserScheduleRepository scheduleRepository) {
        return new DevFestDataSource(api, scheduleRepository);
    }
}
