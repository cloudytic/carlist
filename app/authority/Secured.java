package authority;

import controllers.Auth;
import models.Account;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Optional;

public class Secured extends Security.Authenticator {

    @Override
    public Optional<String> getUsername(Http.Request req) {
        Account account = Auth.getAccount(req);
        if(account != null) {
            return req.session().get(Auth.ACCOUNT);
        }
        return Optional.empty();
    }
    
    @Override
    public Result onUnauthorized(Http.Request req) {
        return redirect(controllers.routes.LoginController.index()+"?lastUrl="+req.uri());
    }   
}