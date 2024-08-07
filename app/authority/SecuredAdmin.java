package authority;

import controllers.Auth;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Optional;

public class SecuredAdmin extends Security.Authenticator {
    
    @Override
    public Optional<String> getUsername(Http.Request req) {
        if(Auth.getAdmin(req) != null) {
            return Optional.of("pass");
        }
        return Optional.empty();
    }
    
    @Override
    public Result onUnauthorized(Http.Request req) {
    	req.session().adding("lastUrl", req.uri());
        return redirect(controllers.routes.AdLogin.index());
    }   
}