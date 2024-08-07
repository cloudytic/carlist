package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Auto;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import pojos.Param;
import search.SearchReq;
import search.SearchRes;
import search.Searcher;
import services.DB;
import utilities.Display;

import javax.inject.Inject;
import java.util.*;

public class ApiListingController extends Controller {
    private final FormFactory formFactory;

    @Inject
    public ApiListingController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result listing(Http.Request request) {
        //System.out.println(new Date().getTime() + ": " +  request.uri());

        SearchReq req = searchReq(request);

        Param param = Util.param(20, request);

        SearchRes res = Searcher.autoSearch(req, param, false);

        List<Auto> list = res.results.getList();
        Long total = res.results.getTotalRowCount();

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", total);

        JsonNode json = Json.toJson(map);
        return ok(json);
    }

    public Result listingAggs(Http.Request request) {
        //System.out.println(new Date().getTime() + ": " +  request.uri());

        SearchReq req = searchReq(request);
        Param param = Util.param(20, request);

        SearchRes res = Searcher.autoSearch(req, param, true);

        Map<String, Object> map = new HashMap<>();
        map.put("aggregations", res.aggregates);

        JsonNode json = Json.toJson(map);
        return ok(json);
    }

    public SearchReq searchReq(Http.Request request) {
        Form<SearchReq> sForm = formFactory.form(SearchReq.class);
        Form<SearchReq> filled = sForm.bindFromRequest(request);

        if(filled.hasErrors()) {}
        SearchReq req = filled.get();
        req.trashed = false;
        req.visible = true;
        req.published = true;
        return req;
    }

    public Result single(Long id, Http.Request request) {

        Auto auto = DB.findOne(Auto.class, id);

        Display display = new Display();
        if(auto.account != null) {
            display.name = auto.account.businessName;
            display.phone = auto.account.phone;
            if(Util.isNotEmpty(auto.account.whatsapp)) {
                display.whatsapp = auto.account.whatsapp();
                display.whatsAppLink = "whatsapp://send?text=Hi, I will like to get more information on this car you listed on Carloaded.com " +
                        Util.website() + "/cars/" + auto.getUrl() + " &phone=" + auto.account.whatsapp();
            }
            display.url = auto.account.url();
        } else if(auto.contact != null) {
            display.name = auto.contact.name;
            display.phone = auto.contact.phone;
            display.logo = auto.contact.logo();
            display.url = auto.contact.url();
        }

        List<Auto> bySeller = ListingController.bySeller(auto, 10);
        List<Auto> allSimilar = ListingController.similar(auto, 30);

        Map<String, Object> map = new HashMap<>();
        map.put("auto", auto);
        map.put("display", display);
        map.put("bySeller", bySeller);
        map.put("allSimilar", allSimilar);

        JsonNode json = Json.toJson(map);

        return ok(json);
    }
}
