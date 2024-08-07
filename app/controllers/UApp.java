package controllers;

import play.mvc.Http;

import java.util.Optional;

public class UApp {
    public static boolean is(Http.Request request) {
        Optional<String> appOp = request.queryString("app");
        if(appOp.isPresent()) {
            String app = appOp.get();
            if("true".equals(app)) {
                return true;
            }
            if("false".equals(app)) {
                return false;
            }
        }
        return false;
    }

    public static boolean isNot(Http.Request request) {
        return !is(request);
    }

}