package controllers;

import au.com.flyingkite.mobiledetect.UAgentInfo;
import play.mvc.Http;

import java.util.Optional;

public class UDevice {
    public static boolean isMobile(Http.Request request) {
        Optional<String> ua = request.header("User-Agent");
        if(ua.isPresent()) {
            UAgentInfo agentInfo = new UAgentInfo(ua.get(), null);
            return agentInfo.detectMobileQuick();
        }
        return false;
    }
    public static boolean isNotMobile(Http.Request request) {
        return !isMobile(request);
    }

    public static boolean isIos(Http.Request request) {
        Optional<String> ua = request.header("User-Agent");
        if(ua.isPresent()) {
            UAgentInfo agentInfo = new UAgentInfo(ua.get(), null);
            return agentInfo.detectIos();
        }
        return false;
    }


    public static boolean isAndroid(Http.Request request) {
        Optional<String> ua = request.header("User-Agent");
        if(ua.isPresent()) {
            UAgentInfo agentInfo = new UAgentInfo(ua.get(), null);
            return agentInfo.detectAndroid();
        }
        return false;
    }

}
