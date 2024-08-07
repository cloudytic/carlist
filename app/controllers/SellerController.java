package controllers;

import models.Account;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import pojos.Param;
import search.SearchReq;
import search.SearchRes;
import search.Searcher;
import services.DB;

import javax.inject.Inject;
import java.util.*;

public class SellerController extends Controller {
    private final FormFactory formFactory;

    @Inject
    public SellerController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index(Http.Request request) {
        Form<SearchReq> sForm = formFactory.form(SearchReq.class);
        Form<SearchReq> filled = sForm.bindFromRequest(request);
        if(filled.hasErrors()) {}
        SearchReq req = filled.get();

        String search = req.search;

        Param param = Util.param(60, request);

        Map<String, Object> ret = new HashMap<>();
        ret.put("param", param);

        //req.active = true;

        SearchRes res = Searcher.accountSearch(req, param, true);

        ret.put("req", req);
        ret.put("res", res);

        return ok(HomeController.prettify(views.html.main.sellerList.render(ret, request)));
    }

    public Result single(String username, Http.Request request) {
        Account account = DB.findOne(Account.class, DB.where().field("username", username));

        if(account == null) {
            return notFound();
        }

        Map<String, Object> ret = new HashMap<>();
        ret.put("seller", account);

        Param param = Util.param(60, request);

        ret.put("param", param);

        SearchReq req = new SearchReq();
        req.trashed = false;
        req.accountId = account.id;

        SearchRes res = Searcher.autoSearch(req, param, false);

        ret.put("req", req);
        ret.put("res", res);

        return ok(HomeController.prettify(views.html.main.seller.render(ret, request)));
    }
}
