package controllers;

import authority.SecuredAdmin;
import models.*;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.*;

import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utilities.AdCrawl;
import utilities.PrImport;

@Security.Authenticated(SecuredAdmin.class)
public class AdCrawler extends Controller {
    private final FormFactory formFactory;
    private final Form<PrImport> form;
    private static Form<PrImport> importForm;

    private static final String uAgent = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";

    @Inject
    public AdCrawler(FormFactory formFactory) {
        this.formFactory = formFactory;
        this.form = formFactory.form(PrImport.class);
        importForm = form;
    }

    public Result add(Http.Request request) {
        return ok(views.html.admin.crawl_import.render(importForm, request));
    }

    public Result create(Http.Request request) {

        Form<PrImport> formData = importForm.bindFromRequest(request);

        if (formData.hasErrors()) {
            return badRequest(views.html.admin.crawl_import.render(formData, request));
        }
        PrImport imp = formData.get();

        String[] urls  = imp.urls.split(",");
        Exec.Ex.execute(new Importer(urls));

        return redirect(routes.AdCrawler.add());

    }

    public Result createFile(Http.Request request) throws Exception {
        DynamicForm requestData = formFactory.form().bindFromRequest(request);

        Http.MultipartFormData body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");
        if (file != null) {
            FileInputStream input = new FileInputStream((File)file.getRef());
            BufferedInputStream bis = new BufferedInputStream(input);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result = bis.read();
            while(result != -1) {
                byte b = (byte)result;
                buf.write(b);
                result = bis.read();
            }
            String hrefs =  buf.toString();
            String[] hrefsList = hrefs.split(",");

            Exec.Ex.execute(new Importer(hrefsList));

            return redirect(routes.AdCrawler.add());

        } else {
            return redirect(routes.AdCrawler.add()).flashing("message","file is empty");
        }

    }

    /*
    public static void autoCrawl() {
        List<String> hrefs = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://jiji.ng/cars").userAgent(uAgent).ignoreHttpErrors(true).get();
            Elements elements = doc.getElementsByClass("b-list-advert-base");
            if(elements.isEmpty()) {
                return;
            }

            elements.forEach(a -> {
                String url = a.attr("abs:href");
                try {
                    if(!url.trim().equals("")) {
                        url = url.split("\\?")[0].trim();
                        hrefs.add(url.trim());
                        System.out.println(url);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] urls  = hrefs.toArray(new String[0]);;
        Exec.Ex.execute(new Importer(urls));
    }*/

    public static void autoCrawl() {
        List<String> hrefs = new ArrayList<>();
        try {
            for(int i = 1; i <= 5; i++) {
                String ong = "https://ong.ng/c_cars";
                ong = (i > 1) ? ong + "?page=" + i : ong;
                Document doc = Jsoup.connect(ong).userAgent(uAgent).ignoreHttpErrors(true).get();
                Elements elements = doc.getElementsByClass("product__item");
                if(!elements.isEmpty()) {
                    elements.forEach(a -> {
                        String url = a.attr("abs:href");
                        try {
                            if (!url.trim().isEmpty()) {
                                url = url.split("\\?")[0].trim();
                                hrefs.add(url.trim());
                                System.out.println(url);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    });
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(hrefs.isEmpty()) {
            return;
        }

        Collections.reverse(hrefs);

        String[] urls  = hrefs.toArray(new String[0]);;
        Exec.Ex.execute(new Importer(urls));
    }

    public static class Importer implements Runnable {
        private String[] urls;
        public Importer(String[] urls) {
            this.urls = urls;
        }
        @Override
        public void run() {
            try {
                for(String url: urls) {
                    String href = url.trim();
                    if(Util.isNotEmpty(href)) {
                        extract(href);
                    }
                }
                System.out.println("FINISHED CRAWLING at " + new Date() + " for " + urls.length + " urls");
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void extract(String href) {
        try {
            System.out.println("\n\n\n"+href);
            Auto auto = DB.findOne(Auto.class, DB.where().field("crawlHref", href));
            if(auto != null) {
                return;
            }
            AdCrawl crawl = crawl1(href);
            auto = create(crawl);
            if(auto != null) {
                auto.category = DB.findOne(AutoCategory.class, DB.where().field("name", "Car"));
                AdAutoCrud.save(auto);
                System.out.println(auto.getRef());
            } else {
                System.out.println("Auto is null: " + href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private static AdCrawl crawl(String href) throws IOException {
        AdCrawl crawl = new AdCrawl();

        crawl.href = href;

        Document doc = Jsoup.connect(href).userAgent(uAgent).ignoreHttpErrors(true).get();

        crawl.caption = doc.select("h1").first().text();
        crawl.description = doc.select(".qa-description-text").text();

        crawl.currency = doc.getElementsByAttributeValue("itemprop", "priceCurrency").first().text().trim();
        String priceS = doc.getElementsByAttributeValue("itemprop", "price").first().text().trim();
        if(priceS.contains("Negotiable")) {
            priceS = priceS.replace("Negotiable", "").trim();
            crawl.negotiable = true;
        }
        try {
            crawl.price = Double.parseDouble(priceS.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String address = doc.select(".qa-region-icon").first().parent().ownText();
        String[] addArr = address.split(",");
        if (addArr.length == 2 || addArr.length == 3) {
            crawl.state = addArr[0].trim();
            crawl.locality = addArr[1].trim();
        }

        Element brand = doc.getElementsByAttributeValue("itemprop", "brand").first();
        if(brand != null) {
            crawl.make = brand.text().trim();
        }

        Element model = doc.getElementsByAttributeValue("itemprop", "model").first();
        if(model != null) {
            crawl.model = model.text().trim();
        }

        Element productionDate = doc.getElementsByAttributeValue("itemprop", "productionDate").first();
        if(productionDate != null) {
            crawl.year = Integer.parseInt(productionDate.text().trim());
        }

        Element fuelType = doc.getElementsByAttributeValue("itemprop", "fuelType").first();
        if(fuelType != null) {
            crawl.fuelType = fuelType.text().trim();
        }

        Element itemCondition = doc.getElementsByAttributeValue("itemprop", "itemCondition").first();
        if(itemCondition != null) {
            crawl.condition = itemCondition.text().trim();
        }

        Element vehicleTransmission = doc.getElementsByAttributeValue("itemprop", "vehicleTransmission").first();
        if(vehicleTransmission != null) {
            crawl.transmission = vehicleTransmission.text().trim();
        }

        Element mileageFromOdometer = doc.getElementsByAttributeValue("itemprop", "mileageFromOdometer").first();
        if(mileageFromOdometer != null) {
            crawl.mileage = mileageFromOdometer.text().trim();
        }

        Element secondCondition = doc.getElementsByAttributeValue("itemprop", "secondCondition").first();
        if(secondCondition != null) {
            crawl.secondCondition = secondCondition.text().trim();
        }

        Element bodyType = doc.getElementsByAttributeValue("itemprop", "bodyType").first();
        if(bodyType != null) {
            crawl.bodyType = bodyType.text().trim();
        }

        Element driveWheelConfiguration = doc.getElementsByAttributeValue("itemprop", "driveWheelConfiguration").first();
        if(driveWheelConfiguration != null) {
            crawl.driveTrain = driveWheelConfiguration.text().trim();
        }

        Element seatingCapacity = doc.getElementsByAttributeValue("itemprop", "seatingCapacity").first();
        if(seatingCapacity != null) {
            try {
                crawl.seats = Integer.parseInt(seatingCapacity.text().trim());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        Element color = doc.getElementsByAttributeValue("itemprop", "color").first();
        if(color != null) {
            crawl.color = color.text().trim();
        }

        Element dateVehicleFirstRegistered = doc.getElementsByAttributeValue("itemprop", "dateVehicleFirstRegistered").first();
        if(dateVehicleFirstRegistered != null) {
            crawl.registered = dateVehicleFirstRegistered.text().trim();
        }

        Element interiorColor = doc.getElementsContainingOwnText("Interior Color").first();
        if(interiorColor != null) {
            interiorColor.siblingElements().forEach(element -> {
                if(element.hasClass("b-advert-attribute__value")) {
                    crawl.interiorColor = element.text().trim();
                }
            });
        }

        Element trim = doc.getElementsContainingOwnText("Trim").first();
        if(trim != null) {
            trim.siblingElements().forEach(element -> {
                if (element.hasClass("b-advert-attribute__value")) {
                    crawl.trim = element.text().trim();
                }
            });
        }

        Element engineSize = doc.getElementsContainingOwnText("Engine Size").first();
        if(engineSize != null) {
            engineSize.siblingElements().forEach(element -> {
                if(element.hasClass("b-advert-attribute__value")) {
                    crawl.engineSize = element.text().trim();
                }
            });
        }

        Element cylinder = doc.getElementsContainingOwnText("Number of Cylinders").first();
        if(cylinder != null) {
            cylinder.siblingElements().forEach(element -> {
                if(element.hasClass("b-advert-attribute__value")) {
                    crawl.cylinder = Integer.parseInt(element.text().trim());
                }
            });
        }

        Element horsePower = doc.getElementsContainingOwnText("Horse Power").first();
        if(horsePower != null) {
            horsePower.siblingElements().forEach(element -> {
                if (element.hasClass("b-advert-attribute__value")) {
                    crawl.horsePower = element.text().trim();
                }
            });
        }

        Elements attributes = doc.select(".b-advert-attributes__tag__container");
        for(Element attr: attributes) {
            crawl.attributes.add(attr.text().trim());
        }

        crawl.contactName = doc.select(".b-seller-block__name").first().text();

        try {
            crawl.contactLogo = doc.select(".b-seller-block__avatar").first().attr("style")
                    .replace("background-image:url(", "")
                    .replace("background-image: url(", "")
                    .replace(");", "")
                    .replace(")", "")
                    .trim();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        *//*
        String html = doc.html();
        String regex = "0[0-9]{10}";
        Matcher m = Pattern.compile(regex).matcher(html);
        while (m.find()) {
            String match = m.group();
            match = match.trim();
            if(Util.isNotEmpty(match)) {
                crawl.contactPhone = match;
                break;
            }
        }*//*


        try {
            String[] arrHr = href.split("/");
            String path = arrHr[arrHr.length - 1];
            String hrefOng = "https://ong.ng/a_"+path;
            System.out.println(hrefOng);
            Document docOng = Jsoup.connect(hrefOng).userAgent(uAgent).ignoreHttpErrors(true).get();
            docOng.select(".details__images__container img").forEach(element -> {
                //System.out.println(element.attr("src"));
                crawl.images.add(element.attr("src"));
            });

            if(crawl.images.isEmpty()) {
                doc.getElementsByTag("picture").forEach(element -> {
                    crawl.images.add(element.getElementsByTag("img").first().attr("src"));
                });
            }

            String ongHtml = docOng.outerHtml();

            Pattern pattern = Pattern.compile("phone:\"\\d{11}\"", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(ongHtml);
            boolean matchFound = matcher.find();
            if(matchFound) {
                String result = matcher.group();
                String phone = result.replace("phone:", "").replaceAll("\"", "").trim();
                crawl.contactPhone = phone;
                System.out.println("Phone: " + phone);
            } else {
                System.out.println("Phone not found");
            }
        } catch (Exception e) {
            doc.getElementsByTag("picture").forEach(element -> {
                crawl.images.add(element.getElementsByTag("img").first().attr("src"));
            });
        }

        System.out.println(crawl);

        return crawl;
    }*/


    private static AdCrawl crawl1(String href) throws IOException {
        AdCrawl crawl = new AdCrawl();

        crawl.href = href;

        Document doc = Jsoup.connect(href).userAgent(uAgent).ignoreHttpErrors(true).get();

        String ongHtml = doc.outerHtml();

        crawl.caption = doc.select("h1").first().text();
        //crawl.description = doc.select(".qa-description-text").text();

        try {
            crawl.currency = doc.getElementsByAttributeValue("itemprop", "priceCurrency").first().text().trim();
            String priceS = doc.getElementsByAttributeValue("itemprop", "price").first().text().trim();
            if (priceS.contains("Negotiable")) {
                priceS = priceS.replace("Negotiable", "").trim();
                crawl.negotiable = true;
            }
            crawl.price = Double.parseDouble(priceS.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            //System.out.println("Price error:" + e.getMessage());

            Pattern pattern = Pattern.compile(",\"₦ \\d{1,3}(,\\d{3})*\",", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(ongHtml);
            boolean matchFound = matcher.find();
            if(matchFound) {
                String result = matcher.group()
                        .replaceAll("\"", "")
                        .replace(",", "");
                String[] priceSt = result.split(" ");
                if(priceSt.length == 2) {
                    crawl.currency = priceSt[0].trim();
                    String priceS = priceSt[1].trim();
                    try {
                        crawl.price = Double.parseDouble(priceS);
                    } catch (Exception e1) {
                        System.out.println("Price error:" + e1.getMessage());
                    }
                }
            } else {
                System.out.println("Price not found");
            }
        }

        String address = doc.select(".details__location").first().getElementsByTag("span").first().ownText();
        String[] addArr = address.split(",");
        if (addArr.length == 2 || addArr.length == 3) {
            crawl.state = addArr[0].trim();
            crawl.locality = addArr[1].trim();
        }

        Element grid = doc.select(".details__description").first();

        try {
            crawl.make = grid.getElementsContainingOwnText("Make").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Make error:" + e.getMessage());
        }

        try {
            crawl.model = grid.getElementsContainingOwnText("Model").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Model error:" + e.getMessage());
        }

        try {
            crawl.year = Integer.parseInt(grid.getElementsContainingOwnText("Year of Manufacture").first().siblingElements().first().ownText());
        } catch (Exception e) {
            System.out.println("Year error:" + e.getMessage());
        }

        try {
            crawl.condition = grid.getElementsContainingOwnText("Condition").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Condition error:" + e.getMessage());
        }

        try {
            crawl.secondCondition = grid.getElementsContainingOwnText("Second Condition").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Second Condition error:" + e.getMessage());
        }

        try {
            crawl.fuelType = grid.getElementsContainingOwnText("Fuel").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Fuel Type error:" + e.getMessage());
        }

        try {
            crawl.transmission = grid.getElementsContainingOwnText("Transmission").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Transmission error:" + e.getMessage());
        }

        try {
            crawl.mileage = grid.getElementsContainingOwnText("Mileage").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Mileage error:" + e.getMessage());
        }

        try {
            crawl.bodyType = grid.getElementsContainingOwnText("Body").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Body Type error:" + e.getMessage());
        }

        try {
            crawl.driveTrain = grid.getElementsContainingOwnText("Drivetrain").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Drivetrain error:" + e.getMessage());
        }

        try {
            crawl.seats = Integer.parseInt(grid.getElementsContainingOwnText("Seats").first().siblingElements().first().ownText());
        } catch (Exception e) {
            System.out.println("Seats error:" + e.getMessage());
        }

        try {
            crawl.color = grid.getElementsContainingOwnText("Color").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Color error:" + e.getMessage());
        }

        try {
            crawl.interiorColor = grid.getElementsContainingOwnText("Interior Color").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Interior Color error:" + e.getMessage());
        }

        try {
            crawl.registered = grid.getElementsContainingOwnText("Registered Car").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Registered error:" + e.getMessage());
        }

        try {
            crawl.trim = grid.getElementsContainingOwnText("Trim").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Trim error:" + e.getMessage());
        }

        try {
            crawl.engineSize = grid.getElementsContainingOwnText("Engine Size").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Engine Size error:" + e.getMessage());
        }

        try {
            crawl.cylinder = Integer.parseInt(grid.getElementsContainingOwnText("Number of Cylinders").first().siblingElements().first().ownText());
        } catch (Exception e) {
            System.out.println("Number of Cylinders error:" + e.getMessage());
        }

        try {
            crawl.horsePower = grid.getElementsContainingOwnText("Horse Power").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Horse Power error:" + e.getMessage());
        }

        try {
            String attributes = grid.getElementsContainingOwnText("Key Features").first().siblingElements().first().ownText();
            String[] attrArr = attributes.split(",");
            for(String attr: attrArr) {
                crawl.attributes.add(attr.trim());
            }
        } catch (Exception e) {
            System.out.println("Key Features error:" + e.getMessage());
        }

        //////////////////////////////////////////////

        try {
            crawl.contactName = doc.getElementsContainingOwnText("about this seller").first().siblingElements().first().ownText();
        } catch (Exception e) {
            System.out.println("Contact Name error:" + e.getMessage());
        }

        try {
            crawl.contactLogo = doc.getElementsByTag("source").first().siblingElements().first().attr("src");
        } catch (Exception e) {
            System.out.println("Logo error" + e.getMessage());
        }

        try {
            doc.select(".details__images__container img").forEach(element -> {
                //System.out.println(element.attr("src"));
                crawl.images.add(element.attr("src"));
            });

            if(crawl.images.isEmpty()) {
                doc.getElementsByTag("picture").forEach(element -> {
                    crawl.images.add(element.getElementsByTag("img").first().attr("src"));
                });
            }

            Pattern pattern = Pattern.compile(",\"0\\d{10}\",", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(ongHtml);
            boolean matchFound = matcher.find();
            if(matchFound) {
                String result = matcher.group();
                String phone = result.replace(",", "").replaceAll("\"", "").trim();
                crawl.contactPhone = phone;
                System.out.println("Phone: " + phone);
            } else {
                System.out.println("Phone not found");
            }
        } catch (Exception e) {
            doc.getElementsByTag("picture").forEach(element -> {
                crawl.images.add(element.getElementsByTag("img").first().attr("src"));
            });
        }

        System.out.println(crawl);

        return crawl;
    }

    private static Auto create(AdCrawl crawl) {
        Auto auto = new Auto();

        if(Util.isEmpty(crawl.contactPhone)) {
            System.out.println("Phone is empty");
            return null;
        }

        auto.uuid = Util.generateUniqueId(50);

        auto.published = true;
        auto.created = new Date();

        auto.crawlHref = crawl.href;

        //Caption
        auto.caption = crawl.caption;

        //Description
        try {
            String description = crawl.description;
            if (Util.isEmpty(description)) {
                description = crawl.caption;
            }
            auto.description = description
                    .replace("\n", "<br/>")
                    .replace("<a", "<span")
                    .replace("</a", "</span")
                    .replace("<A", "<span")
                    .replace("</A", "</span");
        } catch (Exception e) {
        }

        //Price
        auto.price = crawl.price;
        auto.priceCurrency = crawl.currency;
        auto.priceNegotiable = crawl.negotiable;

        //Make//Model
        String modelStr = Util.capitalize(Util.trim(crawl.model));
        if (Util.isEmpty(modelStr)) {
            return null;
        }
        AutoModel model = DB.findOne(AutoModel.class, DB.where().field("name", modelStr));
        if (model == null) {
            return null;

            /*model = new AutoModel(modelStr);
            String makeStr = Util.capitalize(Util.trim(crawl.make));
            if (Util.isEmpty(makeStr)) {
                return null;
            }
            AutoMake make = DB.findOne(AutoMake.class, DB.where().field("name", makeStr));
            if (make == null) {
                make = new AutoMake(makeStr);
                DB.save(make);
            }
            model.parent = make;
            DB.save(model);*/
        }
        auto.model = model;

        //Year
        auto.year = crawl.year;

        //Fuel Type
        String fuelTypeStr = Util.capitalize(Util.trim(crawl.fuelType));
        if (Util.isNotEmpty(fuelTypeStr)) {
            AutoFuelType fuelType = DB.findOne(AutoFuelType.class, DB.where().field("name", fuelTypeStr));
            if (fuelType == null) {
                //fuelType = new AutoFuelType(fuelTypeStr);
                //DB.save(fuelType);
            }
            auto.fuelType = fuelType;
        }

        //Condition
        String conditionStr = Util.capitalize(Util.trim(crawl.condition));
        if(Util.isNotEmpty(conditionStr)) {
            AutoCondition condition = DB.findOne(AutoCondition.class, DB.where().field("name", conditionStr));
            if (condition == null) {
                //condition = new AutoCondition(conditionStr);
                //DB.save(condition);
            }
            auto.condition = condition;
        }

        //Transmission
        String transmissionStr = Util.capitalize(Util.trim(crawl.transmission));
        if(Util.isNotEmpty(transmissionStr)) {
            AutoTransmission transmission = DB.findOne(AutoTransmission.class, DB.where().field("name", transmissionStr));
            if (transmission == null) {
                //transmission = new AutoTransmission(transmissionStr);
                //DB.save(transmission);
            }
            auto.transmission = transmission;
        }

        //Mileage
        try {
            auto.mileage = Integer.parseInt(crawl.mileage.replaceAll("[^0-9.]", "").trim());
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        //Second Condition
        auto.secondCondition = Util.capitalize(Util.trim(crawl.secondCondition));

        //Body Type
        String bodyTypeStr = Util.capitalize(Util.trim(crawl.bodyType));
        if(Util.isNotEmpty(bodyTypeStr)) {
            AutoBodyType bodyType = DB.findOne(AutoBodyType.class, DB.where().field("name", bodyTypeStr));
            if (bodyType == null) {
                //bodyType = new AutoBodyType(bodyTypeStr);
                //DB.save(bodyType);
            }
            auto.bodyType = bodyType;
        }

        //Drive Train
        String driveTrainStr = Util.capitalize(Util.trim(crawl.driveTrain));
        auto.driveTrain = driveTrainStr;
        if (Util.isNotEmpty(driveTrainStr)) {
            AutoDriveTrain drive = DB.findOne(AutoDriveTrain.class, DB.where().field("name", driveTrainStr));
            if (drive == null) {
                //drive = new AutoDriveTrain(driveTrainStr);
                //DB.save(drive);
            }
            auto.drive = drive;
        }


        //Seats
        auto.seats = crawl.seats;

        //Color
        String colorStr = Util.capitalize(Util.trim(crawl.color));
        if(Util.isNotEmpty(colorStr)) {
            AutoColor color = DB.findOne(AutoColor.class, DB.where().field("name", colorStr));
            if (color == null) {
                //color = new AutoColor(colorStr);
                //DB.save(color);
            }
            auto.colorExterior = color;
        }

        //Interior Color
        String interiorColorStr = Util.capitalize(Util.trim(crawl.interiorColor));
        if(Util.isNotEmpty(interiorColorStr)) {
            AutoColor interiorColor = DB.findOne(AutoColor.class, DB.where().field("name", interiorColorStr));
            if (interiorColor == null) {
                //interiorColor = new AutoColor(interiorColorStr);
                //DB.save(interiorColor);
            }
            auto.colorInterior = interiorColor;
        }

        //Registered
        auto.registered = !"No".equalsIgnoreCase(crawl.registered);

        //Trim
        auto.trim = Util.capitalize(Util.trim(crawl.trim));

        //Engine Size
        auto.engineSize = crawl.engineSize;

        //Cylinder
        auto.cylinder = crawl.cylinder;

        //Horse Power
        auto.horsePower = crawl.horsePower;

        //Features
        for(String featureStr: crawl.attributes) {
            String featureName = Util.capitalize(Util.trim(featureStr));
            if(Util.isNotEmpty(featureName)) {
                AutoFeature feature = DB.findOne(AutoFeature.class, DB.where().field("name", featureName));
                if(feature == null) {
                    //feature = new AutoFeature(featureName);
                    //DB.save(feature);
                } else
                    auto.features.add(feature);
            }
        }

        //Locality
        String localityStr = Util.capitalize(Util.trim(crawl.locality));
        if(Util.isEmpty(localityStr)) return null;
        AddressLocality locality = DB.findOne(AddressLocality.class, DB.where().field("name", localityStr));
        if(locality == null) {
            return null;
            /*locality = new AddressLocality(localityStr);

            String stateStr = Util.capitalize(Util.trim(crawl.state));
            if(Util.isEmpty(stateStr)) return null;
            AddressState state = DB.findOne(AddressState.class, DB.where().field("name", stateStr));
            if(state == null) {
                //state = new AddressState(stateStr);
                //DB.save(state);
                return null;
            }
            locality.parent = state;
            DB.save(locality);*/
        }
        auto.locality = locality;


        //Contact
        Contact contact = new Contact();
        contact.name = crawl.contactName;
        contact.phone = crawl.contactPhone;
        if(Util.isNotEmpty(crawl.contactLogo)) {
            try {
                String logoName = Util.makeSlug(contact.name) + "-crl-" + Util.generateUniqueId(20) + "." + S3Service.getFileExtension(crawl.contactLogo);

                InputStream input = new URL(crawl.contactLogo).openConnection().getInputStream();

                File logoOrigin = new File(S3Service.TMPLOGODIR + logoName);
                FileUtils.copyInputStreamToFile(input, logoOrigin);
                contact.logoUrl = S3Service.storeLogo(logoOrigin, logoName, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DB.save(contact);
        auto.contact = contact;


        //Images
        int i = 0;
        for(String imageUrl: crawl.images) {
            try {
                String fileName = Util.makeSlug(auto.caption) + "-cr-" + Util.generateUniqueId(20) + "." + S3Service.getFileExtension(imageUrl);

                InputStream input = new URL(imageUrl).openConnection().getInputStream();

                File origin = new File(S3Service.TMPMAINDIR + fileName);

                FileUtils.copyInputStreamToFile(input, origin);

                S3Service.storePicture(origin, fileName, -1, false);

                AutoPicture picture = new AutoPicture(fileName);
                picture.uuid = auto.uuid;
                picture.sort = i;
                DB.save(picture);

                i++;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return auto;
    }
}
