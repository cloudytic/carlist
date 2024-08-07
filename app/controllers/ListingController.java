package controllers;

import models.Auto;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import pojos.Param;
import pojos.SellerContact;
import search.SearchReq;
import search.SearchRes;
import search.Searcher;
import services.DB;
import services.PidCalculator;
import utilities.Display;

import javax.inject.Inject;
import java.util.*;

public class ListingController extends Controller {
    private final FormFactory formFactory;

    @Inject
    public ListingController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public static boolean isRef(String text) {
        return Util.isNotEmpty(text) && text.trim().matches("\\d[A-Za-z]{5}");
    }

    public Result listing(String path, Http.Request request) {
        if(path.matches(".+-\\d+") && !path.contains("/")) {
            return single(path, request);
        }


        String uri =  request.uri();
        if(uri.contains("?&")) {
            return redirect(uri.replace("?&", "?"));
        }

        Form<SearchReq> sForm = formFactory.form(SearchReq.class);
        Form<SearchReq> filled = sForm.bindFromRequest(request);
        if(filled.hasErrors()) {}
        SearchReq req = filled.get();

        String search = req.search;
        if(Util.isEmpty(path) && isRef(search)){
            return single(search.trim(), request);
        }

        if(Util.isNotEmpty(path)) {
            if(path.endsWith("/in")) {
                return redirect(routes.ListingController.listing(path.substring(0, path.length() - 3)));
            }
            if(path.endsWith("/in/")) {
                return redirect(routes.ListingController.listing(path.substring(0, path.length() - 4)));
            }

            String lPath = "";
            String mPath = "";
            if(path.startsWith("in/")) {
                lPath = path.replaceFirst("in/", "");
            } else if(path.contains("/in/")) {
                mPath = path.split("/in/")[0];
                lPath = path.split("/in/")[1];
            } else {
                mPath = path;
            }


            if(Util.isNotEmpty(mPath)) {
                String[] array = mPath.split("/");
                int length = array.length;

                String index0;
                String index1;
                String index2;

                boolean in0 = true;
                boolean in1 = true;
                boolean in2 = true;

                switch (length) {
                    case 1:
                        index0 = array[0];
                        if(USelect.indexMakes().contains(index0)) {
                            req.make = index0;
                        } else if(USelect.indexBodyTypes().contains(index0)) {
                            req.bodyType = index0;
                        } else {
                            in0 = false;
                        }
                        break;
                    case 2:
                        index0 = array[0];
                        index1 = array[1];
                        if(USelect.indexMakes().contains(index0)) {
                            req.make = index0;
                        } else {
                            in0 = false;
                        }
                        if (USelect.indexModels().contains(index1)) {
                            req.model = index1;
                        } else if(USelect.indexBodyTypes().contains(index1)) {
                            req.bodyType = index1;
                        } else {
                            in1 = false;
                        }
                        break;
                    case 3:
                        index0 = array[0];
                        index1 = array[1];
                        index2 = array[2];
                        if(USelect.indexMakes().contains(index0)) {
                            req.make = index0;
                        } else {
                            in0 = false;
                        }
                        if (USelect.indexModels().contains(index1)) {
                            req.model = index1;
                        } else {
                            in1 = false;
                        }
                        if(USelect.indexBodyTypes().contains(index2)) {
                            req.bodyType = index2;
                        } else {
                            in2 = false;
                        }
                        break;
                    default:
                        break;
                }

                if(!in0 || !in1 || !in2) {
                    return redirect(routes.ListingController.listing(""));
                }
            }

            if(Util.isNotEmpty(lPath)) {
                String[] array = lPath.split("/");
                int length = array.length;
                String state = array[0];
                if(USelect.indexStates().contains(state)) {
                    req.state = state;
                }
                if(length > 1) {
                    String locality = array[1];
                    if (USelect.indexLocalities().contains(locality)) {
                        req.locality = locality;
                    }
                }
            }
        }

        Param param = Util.param(60, request);

        Map<String, Object> ret = new HashMap<>();
        ret.put("param", param);

        req.trashed = false;
        req.published = true;

        req.visible = true;

        SearchRes res = Searcher.autoSearch(req, param, true);

        if(res.results.isEmpty()) {
            SearchReq allReq = new SearchReq();
            allReq.trashed = false;
            allReq.published = true;

            allReq.visible = true;
            res = Searcher.autoSearch(allReq, param, true);
        }

        ret.put("req", req);
        ret.put("res", res);

        return ok(HomeController.prettify(views.html.main.listing.render(ret, request)));
    }

    private Result single(String path, Http.Request request) {
        String[] ar = path.split("-");
        String sid = ar[ar.length - 1];
        Auto auto = DB.findOne(Auto.class, sid);

        if(isRef(path)){
            Long id = PidCalculator.getId(path);
            auto = DB.findOne(Auto.class, id);
        }

        if(auto == null) {
            return notFound();
        }

        if(!Auth.isAdmin(request) && (auto.trashed || !auto.published)) {
            return notFound();
        }

        if(!path.equals(auto.getUrl())) {
            return redirect(routes.ListingController.listing(auto.getUrl()));
        }

        if(auto.notVisible) {
            return redirect(routes.ListingController.listing(""));
        }

        Map<String, Object> ret = new HashMap<>();
        ret.put("auto", auto);
        Display display = new Display();
        if(auto.account != null) {
            display.name = auto.account.businessName;
            display.phone = auto.account.phone;
            if(Util.isNotEmpty(auto.account.whatsapp)) {
                display.whatsapp = auto.account.whatsapp();
                display.whatsAppLink = "whatsapp://send?text=Hi, I will like to get more information on this car you listed on {placeholder} " +
                        Util.website() + "/cars/" + auto.getUrl() + " &phone=" + auto.account.whatsapp();
            }
            display.url = auto.account.url();
        } else if(auto.contact != null) {
            display.name = auto.contact.name;
            display.phone = auto.contact.phone;
            display.logo = auto.contact.logo();
            display.url = routes.ListingController.listing(auto.getUrl()).toString();//auto.contact.url();
        }
        ret.put("display", display);

        List<Auto> bySeller = bySeller(auto, 10);
        ret.put("bySeller", bySeller);

        List<Auto> allSimilar = similar(auto, 30);
        //int end = Math.min(allSimilar.size(), 9);
        //List<Auto> similar = allSimilar.subList(0, end);
        ret.put("similar", allSimilar);

        return ok(HomeController.prettify(views.html.main.single.render(ret, request)));
    }

    public static List<Auto> similar(Auto auto, int total) {
        double price = auto.price;
        SearchReq req = new SearchReq();

        if(auto.model != null) {
            req.model = auto.model.slug;
            req.make = auto.model.parent.slug;
        }

        req.search = auto.getCaption() + auto.getHeader(true) + " " + auto.address();
        req.trashed = false;
        req.published = true;

        req.visible = true;

        req.priceRange =  (price - (0.5 * price)) + "-" + (price + (0.5 * price));

        req.yearRange = (auto.year - 3) + "-" + (auto.year + 3);

        List<Auto> indexList = Searcher.autoSearch(req, new Param(0, total), false).results.getList();

        List<Auto> similar = new ArrayList<>();
        for (Auto index : indexList) {
            if (!Objects.equals(index.id, auto.id)) {
                similar.add(index);
            }
        }

        return similar;
    }

    public static List<Auto> bySeller(Auto auto, int total) {
        SearchReq req = new SearchReq();
        req.trashed = false;
        req.published = true;
        req.visible = true;
        if(auto.account != null) {
            req.accountId = auto.account.id;
        } else if(auto.contact != null) {
            req.contactPhone = auto.contact.phone;
        } else {
            return new ArrayList<>();
        }

        List<Auto> indexList = Searcher.autoSearch(req, new Param(0, total), false).results.getList();

        List<Auto> bySeller = new ArrayList<>();
        for (Auto index : indexList) {
            if (!Objects.equals(index.id, auto.id)) {
                bySeller.add(index);
            }
        }

        return bySeller;
    }

    public static List<Auto> featured() {
        SearchReq req = new SearchReq();
        req.trashed = false;
        req.published = true;
        req.priceUp = "10000000";

        req.visible = true;
        SearchRes res = Searcher.autoSearch(req, Param.getSome(6, "created", "desc"), false);
        return res.results.getList();
    }

    public Result sellers(Http.Request request) {
        if(true) {
            return redirect(routes.ListingController.listing(""));
        }

        Map<String, Object> ret = new HashMap<>();

        Param param = Util.param(60, request);
        ret.put("param", param);

        SearchRes res = Searcher.sellerContacts(param);

        ret.put("sellers", res.resultSellerContacts.getList());
        ret.put("count", res.resultSellerContacts.getTotalRowCount());

        return ok(HomeController.prettify(views.html.main.sellersAgg.render(ret, request)));
    }

    public Result seller(String code, Http.Request request) {
        SearchReq req = new SearchReq();

        String phone = SellerContact.getPhone(code);
        req.contactPhone = phone;
        SellerContact sellerContact = Searcher.getSellerContact(phone);

        Param param = Util.param(60, request);

        Map<String, Object> ret = new HashMap<>();
        ret.put("param", param);

        req.trashed = false;
        req.visible = true;

        SearchRes res = Searcher.autoSearch(req, param, true);

        ret.put("req", req);
        ret.put("res", res);
        ret.put("sellerContact", sellerContact);

        return ok(HomeController.prettify(views.html.main.listing.render(ret, request)));
    }

    public static void indexSellerContacts() {
        SearchRes res = Searcher.autoSellerContactsAgg();
        res.getAggregates().get("contact").forEach(bucket -> {
            SellerContact contact = new SellerContact();
            String phone = bucket.term;
            long count = bucket.count;
            contact.phone = phone;
            contact.autoCount = count;
            Auto auto = DB.findOne(Auto.class, DB.where().field("contact.phone", phone));
            if (auto != null) {
                contact.name = auto.contact.name;
                contact.logo = auto.contact.logo();
            }
            Searcher.indexSellerContact(contact);
            System.out.println("Indexed Seller Contact: " + contact.phone);
        });
    }


    public Result indexAutosByIds(Http.Request request) {
        String ids = request.queryString("ids").orElse("");
        if(Util.isNotEmpty(ids)) {
            String[] array = ids.split(",");
            for(String id: array) {
                id = id.trim();
                if(Util.isNumeric(id)) {
                    Auto auto = DB.findOne(Auto.class, id);
                    if(auto != null) {
                        Searcher.indexAuto(auto);
                        System.out.println("Indexed Auto By Id: " + auto.id);
                    }
                }
            }
        }
        return ok("ok");
    }
}
