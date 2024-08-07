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

    public Result about(Http.Request request) {
        return ok(prettify(views.html.main.about.render(request)));
    }

    public Result contact(Http.Request request) {
        return ok(prettify(views.html.main.contact.render(request)));
    }

    public Result terms(Http.Request request) {
        return ok(views.html.main.terms.render(request));
    }

    public Result policy(Http.Request request) {
        return ok(views.html.main.policy.render(request));
    }

    public Result requestAcctDeletion(Http.Request request) {
        return ok(views.html.main.requestAcctDeletion.render(request));
    }


    public Result unTrail(String path) {
        return movedPermanently("/" + path);
    }

    public static Html prettify(Html content) {
        return new Html(Jsoup.parse(content.body()).outerHtml());
    }

}
