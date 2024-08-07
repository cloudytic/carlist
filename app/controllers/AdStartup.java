package controllers;

import authority.SecuredAdmin;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.Exec;

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

        return redirect(routes.AdAdmin.index());
    }

    public static void indexSearch() {
        Exec.Ex.execute(AdAutoCrud::indexAutos);
    }

    public static void indexAccounts() {
        Exec.Ex.execute(AdAcctCrud::indexAccounts);
    }
}
