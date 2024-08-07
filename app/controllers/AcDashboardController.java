package controllers;

import authority.Secured;
import models.Account;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class AcDashboardController extends Controller {
    public Result index(Http.Request request) {
        Account account = Auth.getAccount(request);
        if(Util.isEmpty(account.businessName) || Util.isEmpty(account.phone)) {
            return redirect(routes.AcProfileController.edit())
                    .flashing("flashed", "Please complete your profile first.");
        }
        //return ok(views.html.dash.index.render(request));
        return AcAutoController.listings(request);
    }
}
