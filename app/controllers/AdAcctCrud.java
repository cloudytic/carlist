package controllers;

import authority.SecuredAdmin;
import models.Account;
import models.ModelBase;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Needed;
import pojos.Param;
import search.Searcher;
import services.DB;
import utilities.BankList;

import java.lang.reflect.Field;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@Security.Authenticated(SecuredAdmin.class)
public class AdAcctCrud extends Controller {

    public Result index(Http.Request request) {
        Param param = Util.param(50, request);
        param.setSort("id");
        param.setOrder("desc");

        DB.Filter filter = DB.where();

        Optional<String> searchOp = request.queryString("search");
        if(searchOp.isPresent()) {
            String search = searchOp.get().trim();
            if(Util.isNotEmpty(search)) {
                filter.brS().field("businessName").like(search)
                        .or().field("firstName").like(search)
                        .or().field("lastName").like(search)
                        .or().field("email").like(search)
                        .or().field("username").like(search)
                        .or().field("phone").like(search)
                        .or().field("whatsapp").like(search)
                        .brE();
            }
        }

        Optional<String> activeOp = request.queryString("active");
        if(activeOp.isPresent()) {
            String active = activeOp.get().trim();
            if("true".equals(active)) {
                filter.field("active").eq(true);
            } else if("false".equals(active)){
                filter.field("active").eq(false);
            }
        }

        Optional<String> stateOp = request.queryString("state");
        if(stateOp.isPresent()) {
            String id = stateOp.get().trim();
            if(Util.isNumeric(id)) {
                filter.field("locality.parent.id").eq(Long.parseLong(id));
            }
        }

        List<String> filters = Arrays.asList("locality");

        request.queryString().forEach((key,v) -> {
            String value = v[0];
            if(filters.contains(key) && Util.isNumeric(value)) {
                filter.field(key+".id").eq(Long.parseLong(value));
            }
        });


        List<Account> list = DB.find(Account.class, filter, param);
        Long total = DB.count(Account.class, filter);

        Map<String, Object> ret = new HashMap<>();
        ret.put("accounts", list);
        ret.put("total", total);
        ret.put("param", param);

        return ok(views.html.admin.accounts.render(ret, request));
    }

    public Result activate(Long id) {
        Account account = DB.findOne(Account.class, id);
        account.active = !account.active;
        saveAccount(account);
        return ok("success");
    }

    public Result delete(Long id) {
        DB.delete(Account.class, id);
        return ok("success");
    }

    public static void saveAccount(Account account) {
        DB.save(account);
        Searcher.indexAccount(account);
    }

    public static <T extends ModelBase> Form<T> checkRequired(Form<T> filledForm, String field) {
        String value = filledForm.field(field).value().orElse("");
        if (Util.isEmpty(value)) {
            return filledForm.withError(field, "This field is required");
        }
        return filledForm;
    }

    public static <T extends ModelBase> FilledLevel checkFilled(T t) {
        FilledLevel filledLevel = new FilledLevel(0, 0);
        if (t == null) {
            return filledLevel;
        }

        int total = 0;
        int filled = 0;
        for (Field field : t.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Needed.class)) {
                total++;


                try {
                    Object obj = field.get(t);

                    if (obj instanceof String && Util.isNotEmpty((String) obj)) {
                        filled++;
                    } else if (obj instanceof Integer && (Integer) obj != 0) {
                        filled++;
                    } else if (obj instanceof Boolean && (Boolean) obj) {
                        filled++;
                    } else if (obj instanceof Long && (Long) obj != 0L) {
                        filled++;
                    } else if (obj instanceof Double && (Double) obj != 0D) {
                        filled++;
                    } else if (obj != null) {
                        filled++;
                    }
//
//                    if(t instanceof Employment){
//                        System.out.println(t.getClass() + "/"+ field.getName() +"/"+obj );}

                } catch (IllegalAccessException | NullPointerException e) {

                }
                //System.out.println(verifiable.getClass().getName());
            }
        }
        return new FilledLevel(total, filled);
    }

    public static class FilledLevel {
        public int total;
        public int filled;

        public FilledLevel(int total, int filled) {
            this.total = total;
            this.filled = filled;
        }

        public double percent() {
            if (total == 0) return 100;
            return ((double) filled / total) * 100;
        }
    }

    public static class Logo implements Comparable<Logo> {
        public String title;
        public String filename;
        public String url;
        public List<String> category = new ArrayList<>();

        @Override
        public int compareTo(Logo o) {
            return o.title.compareTo(this.title);
        }
    }

    public static String generateUsername(String name, int add) {
        String username = makeUsername(name);
        if(add != 0){
            username += add;
        }
        Account account = DB.findOne(Account.class, DB.where().field("username", username));
        if(account==null){
            return username;
        }
        return generateUsername(name, add + 1);
    }

    public static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    public static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String makeUsername(String text) {
        if(Util.isEmpty(text)) return "";
        String input = text.trim().replace("-", " ").replaceAll(" +", " ");
        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH)
                .replaceAll(" ", "")
                .replaceAll("-+", "");
    }

    public static String cleanNumber(String number) {
        if(Util.isEmpty(number)) {
            return number;
        }

        number = number.trim();

        String no;
        if(number.startsWith("+")) {
            no = number.replaceAll("\\D", "");
            no = "+" + no;
        } else {
            no = number.replaceAll("\\D", "");
        }

        if(no.length() > 15) {
            return number;
        }

        number = no;

        if (number.startsWith("234")) {
            number = number.substring(3);
            number = number.startsWith("0") ? number : "0" + number;
        }

        if (number.startsWith("+234")) {
            number = number.substring(4);
            number = number.startsWith("0") ? number : "0" + number;
        }

        return number;
    }


    public static Account fillAccount(Account account, Account form) {


        return account;
    }

    public static Map<String,String> bankNames(){
        return BankList.getBanksName();
    }

    public static List<Logo> getBanks() {
        return BankList.getBanks();
    }


    public static void indexAccounts() {
        try {
            for(int i=0; i < Integer.MAX_VALUE; i++) {
                Param param = Param.get(i, 50, "created", "desc");

                List<Account> accounts = DB.find(Account.class, DB.where(), param);

                if(accounts.isEmpty()) {
                    break;
                } else {
                    for(final Account account: accounts) {
                        try {
                            Searcher.indexAccount(account);
                            System.out.println("Indexed Account " + account);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }

}
