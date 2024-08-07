package controllers;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import models.Auto;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import pojos.Param;
import search.SearchReq;
import search.SearchRes;
import search.Searcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SiteMapController extends Controller {

    private static String A_SITEMAP;

    public Result autos(Http.Request request) throws IOException {
        if(A_SITEMAP == null) {
            doAutos(request);
        }
        return ok(A_SITEMAP).as("application/xml");
    }

    public static void doAutos(Http.Request request) throws IOException {
        System.out.println("Setting A_SITEMAP");

        SearchReq req = new SearchReq();

        Optional<String> yearOp = request.queryString("year");
        if(yearOp.isPresent()) {
            String year = yearOp.get().trim();
            if(Util.isNumeric(year)) {
                req.year = Integer.parseInt(year);
            }
        }

        Optional<String> stateOp = request.queryString("state");
        if(stateOp.isPresent()) {
            String state = stateOp.get().trim();
            if(Util.isNotEmpty(state)) {
                req.state = state;
            }
        }

        Optional<String> makeOp = request.queryString("make");
        if(makeOp.isPresent()) {
            String make = makeOp.get().trim();
            if(Util.isNotEmpty(make)) {
                req.make = make;
            }
        }

        List<String> filters = Arrays.asList(
                "bodyType",
                "transmission",
                "fuelType",
                "colorExterior",
                "colorInterior",
                "condition",
                "model",
                "locality");

        request.queryString().forEach((key,v) -> {
            String value = v[0];
            if(filters.contains(key) && Util.isNotEmpty(value)) {
                try {
                    req.getClass().getDeclaredField(key).set(req, value);
                } catch (Exception e) {
                    System.out.println("Error setting "+key+" to "+value);
                }
            }
        });

        req.trashed = false;
        req.published = true;

        Param param = Util.param(10000, request);
        param.setPage(0);
        param.setSort("created");
        param.setOrder("desc");

        SearchRes res = Searcher.autoSearch(req, param, false);

        WebSitemapGenerator wsg = new WebSitemapGenerator(Util.website());

        for (Auto auto : res.results.getList()) {
            WebSitemapUrl url = new WebSitemapUrl.Options(Util.website()+"/cars/"+auto.getUrl())
                    .lastMod(auto.created)
                    //.priority(1.0)
                    .build();
            wsg.addUrl(url);
        }

        List<String> list = wsg.writeAsStrings();

        A_SITEMAP = list.get(0);
    }
}