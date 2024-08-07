package services;

import com.typesafe.config.Config;
import play.Environment;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Singletons {
    public static Environment environment;
    public static WSClient ws;
    public static Config config;

    @Inject
    public Singletons(Environment playEnvironment,
                      WSClient wsClient,
                      Config playConfig
    ) {
        environment = playEnvironment;
        ws = wsClient;
        config = playConfig;
    }
}
