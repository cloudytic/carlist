package controllers;

import org.jsoup.Jsoup;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;

public class HomeController extends Controller {

    public Result index(Http.Request request) {
        return ok(prettify(views.html.main.index.render(request)));
    }

    public Result unTrail(String path) {
        return movedPermanently("/" + path);
    }

    public static Html prettify(Html content) {
        return new Html(Jsoup.parse(content.body()).outerHtml());
    }

}
