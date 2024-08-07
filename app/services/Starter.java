package services;

import controllers.AdAutoCrud;
import controllers.AdCrawlScraper;
import controllers.AdCrawler;
import controllers.ListingController;
import models.CronTimer;
import play.Environment;
import play.Mode;
import pojos.Param;
import utilities.CronUtil;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class Starter {

    @Inject
    public Starter(Environment environment) {
        if (environment.mode() == Mode.DEV) {
            //doCrawl();

            //doScrape();
            //autoScrape();

        } else {
            doCrawl();

            //doScrape();
            //autoScrape();

            Exec.ScEx.scheduleAtFixedRate(AdAutoCrud::indexAutos,
                    CronUtil.nextExecutionInHours(21, 0), 24, TimeUnit.HOURS);

            Exec.ScEx.scheduleAtFixedRate(ListingController::indexSellerContacts,
                    CronUtil.nextExecutionInHours(22, 0), 12, TimeUnit.HOURS);

        }
    }

    private static ScheduledFuture<?> scheduledFuture = null;

    public static void doCrawl() {
        scheduledFuture = Exec.ScEx.scheduleAtFixedRate(
                () -> {
                    try {
                        AdCrawler.autoCrawl();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ,
                0, 10, TimeUnit.MINUTES);
    }

    public static void restartCrawl() {
        if(scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        doCrawl();
    }

    public static void doScrape() {
        Exec.Ex.execute(() -> {
            AdCrawlScraper.connect();
            AdCrawlScraper.scrape(1, Param.getSome(50, "created", "desc"));
        });
    }

    public static void autoScrape() {
        Exec.ScEx.scheduleAtFixedRate(
                () -> {
                    System.out.println("Auto Scraping..." + new Date());
                    AdCrawlScraper.scrape(1, Param.getSome(50, "created", "desc"));
                    System.out.println("Done Auto Scraping..." + new Date());
                },
                10, 10, TimeUnit.MINUTES);
    }

    public static void run24(Runnable runnable, int hour) {
        Exec.ScEx.scheduleAtFixedRate(
                runnable,
                CronUtil.nextExecutionInHours(hour, 0), 24, TimeUnit.HOURS
        );
    }

    public static boolean shouldRun(String name, int daysInterval) {
        CronTimer timer = DB.findOne(CronTimer.class, DB.where().field("name", name));

        if(timer == null) {
            timer = new CronTimer(name, new Date());
            DB.save(timer);
            return true;
        }

        Date lastRun = timer.lastRun;
        Date today = new Date();
        long noOfDaysBetween = ChronoUnit.DAYS.between(lastRun.toInstant(), today.toInstant());
        System.out.println(name + "/NOD: " + noOfDaysBetween);
        if(noOfDaysBetween >= daysInterval) {
            timer.lastRun = today;
            DB.save(timer);
            return true;
        }

        return false;
    }

    @PreDestroy
    public void destroy() {

    }
}
