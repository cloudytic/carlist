import com.google.inject.AbstractModule;
import java.time.Clock;

import play.data.format.Formatters;

import formatters.FormattersProvider;
import services.*;

public class Module extends AbstractModule {

    @Override
    public void configure() {
        bind(Clock.class).toInstance(Clock.systemDefaultZone());

        bind(DB.class).asEagerSingleton();
        bind(PasswordService.class).asEagerSingleton();
        bind(EncryptionService.class).asEagerSingleton();
        bind(Singletons.class).asEagerSingleton();
        bind(S3Service.class).asEagerSingleton();
        bind(CacheService.class).asEagerSingleton();
        bind(Starter.class).asEagerSingleton();
        bind(CustomWsClient.class).asEagerSingleton();
        bind(Formatters.class).toProvider(FormattersProvider.class);
    }

}
