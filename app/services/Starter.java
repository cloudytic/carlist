package services;

import controllers.AdAutoCrud;
import controllers.ListingController;
import play.Environment;
import play.Mode;
import utilities.CronUtil;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
public class Starter {

    @Inject
    public Starter(Environment environment) {
        if (environment.mode() == Mode.DEV) {
            //do stuff locally
        } else {

            Exec.ScEx.scheduleAtFixedRate(AdAutoCrud::indexAutos,
                    CronUtil.nextExecutionInHours(21, 0), 24, TimeUnit.HOURS);

            Exec.ScEx.scheduleAtFixedRate(ListingController::indexSellerContacts,
                    CronUtil.nextExecutionInHours(22, 0), 12, TimeUnit.HOURS);

        }
    }

    @PreDestroy
    public void destroy() {

    }
}
