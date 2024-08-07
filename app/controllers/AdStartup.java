package controllers;

import authority.SecuredAdmin;
import models.Auto;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Param;
import search.Searcher;
import services.DB;
import services.Exec;
import services.Starter;

import java.util.List;

@Security.Authenticated(SecuredAdmin.class)
public class AdStartup extends Controller {

    public Result startup(Http.Request request) {
        String d = request.getQueryString("do");
        if(d == null) d = "";

        if(d.equals("indexSearch")) {
            indexSearch();
        }

        if(d.equals("indexAccounts")) {
            indexAccounts();
        }

        if(d.equals("doScrape")) {
            Starter.doScrape();
        }

        if(d.equals("restartCrawl")) {
            Starter.restartCrawl();
        }

        return redirect(routes.AdAdmin.index());
    }

    public static void indexSearch() {
        Exec.Ex.execute(AdAutoCrud::indexAutos);
    }

    public static void indexAccounts() {
        Exec.Ex.execute(AdAcctCrud::indexAccounts);
    }
}
