package controllers;


import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import play.filters.csrf.CSRF;
import play.mvc.Http;
import play.twirl.api.Html;
import pojos.Param;
import utilities.DateUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {
    public static String website() {
        return "https://carloaded.com";
    }

    public static Html sanitizeHtml(String html) {
        String safe = Jsoup.clean(html, Whitelist.basic());
        return Html.apply(safe);
    }

    public static boolean isNotEmpty(String text) {
        return StringUtils.isNotEmpty(text);
    }
    public static boolean isEmpty(String text) {
        return StringUtils.isEmpty(text);
    }
    public static boolean isNumeric(String text) {
        return NumberUtils.isCreatable(text);
    }

    public static Param getParam(Http.Request request) {
        return param(50, request);
    }

    public static Param param(int lim, Http.Request request) {
        int page = 0;
        try {
            String p = getQuery("page", request);
            if(StringUtils.isNumeric(p))
                page = Integer.parseInt(p);
        } catch(Exception e)  {}

        int limit = lim;
        try {
            String l = getQuery("limit", request);

            if(StringUtils.isNumeric(l))
                limit = Integer.parseInt(l);
        } catch(Exception e)  {}

        Param param = new Param(page, limit);
        String sort = getQuery("sort", request);
        if(Util.isNotEmpty(sort)) {
            param.setSort(sort);
        }

        String order = getQuery("order", request);
        if(Util.isNotEmpty(order)) {
            param.setOrder(order);
        }
        return param;
    }

    public static String getQuery(String query, Http.Request request) { return request.queryString(query).orElse(""); }

    public static String  putUriParam(String uri, String param, String value) {
        if(uri.contains(param)) {
            return uri.replaceAll(param+"=\\w*", param+"="+value);
        } else {
            if(uri.contains("?")) {
                return uri + "&" + param + "=" + value;
            } else {
                return uri + "?" + param + "=" + value;
            }
        }
    }

    public static boolean isImage(String uri) {
        String path = uri.toLowerCase();
        return path.contains("jpg")
                || path.contains("png")
                || path.contains("gif")
                || path.contains("jpeg")
                || path.contains("svg");
    }

    public static long remainingTime(Date date, int duration) {
        return DateUtil.remainingTime(date, duration);
    }

    public static String formatDate(LocalDateTime dt, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return dt.format(formatter);
    }

    public static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    public static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String makeSlug(String text) {
        if(isEmpty(text)) return "";
        String input = text.trim().replace("-", " ").replaceAll(" +", " ");
        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH).replaceAll(" ", "-").replaceAll("-+", "-");
    }

    public static String summarize(String text, int size) {
        List<String> list = Arrays.asList(text.split(" "));
        if(list.size() > size) {
            list = list.subList(0, size);
            StringBuilder b = new StringBuilder();
            list.forEach(e -> {
                b.append(e).append(" ");
            });
            return b.toString();
        } else {
            return text;
        }
    }

    public static String summarize1(String text) {
        BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.US);
        boundary.setText(text);
        int start = boundary.first();
        int end = boundary.next();

        if(start < 0) start = 0;
        if(end > text.length()) end = text.length();

        return text.substring(start, end);
    }

    public static String generateUniqueId(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String capitalize(String input) {
        if(input == null) return "";
        input = input.trim().replaceAll("_+", " ").replaceAll("-+", " ").replaceAll(" +", " ");
        return WordUtils.capitalizeFully(input);
    }

    public static String reduceString (String text, int len) {
        if(text == null) return "";
        String val = "";
        if(text.length() > len) {
            val = text.substring(0,len);
            val += "...";
            return val;
        }
        return text;
    }

    public static boolean stringToBoolean(String trueOrFalse) {
        if(isEmpty(trueOrFalse)) return false;
        return BooleanUtils.toBoolean(trueOrFalse);
    }

    public static Date getStartOfDay(Date date) {
        return DateUtil.removeTime(date);
    }
  
    public static Date africanDateTime (Date date) {
        ZoneId zoneId = ZoneId.of("Africa/Lagos");
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    public static String formatDate(Date date, String pattern){
        if(date == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public static String redact(String text, int no) {
        String ret = "";
        if(isNotEmpty(text)) {
            if(text.length() > no) {
                ret = text.substring(0, no) + "..";
            } else {
                ret = text + "..";
            }
        }
        return ret;
    }

    private static final String[] suffix = new String[]{"","K", "M", "B", "T"};

    public static String formatShort(double number) {
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        int MAX_LENGTH = 4;
        while(r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")){
            r = r.substring(0, r.length()-2) + r.substring(r.length() - 1);
        }
        return r;
    }

    public static String formatPrice(double number) {
        return new DecimalFormat("#,###").format(number);
    }

    public static SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy");

    public static String getStartEndDate(Date date, boolean start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int day;
        if(start) {
            day = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        } else {
            day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        String[] arr = monthFormat.format(date).split(" ");
        return arr[0] + " " + day + ", " + arr[1];
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(String password) {
        /*
        (?=.*[0-9]) represents a digit must occur at least once.
        (?=.*[a-z]) represents a lower case alphabet must occur at least once.
        (?=.*[A-Z]) represents an upper case alphabet that must occur at least once.
        (?=.*[@#$%^&-+=()] represents a special character that must occur at least once.
        (?=\\S+$) white spaces don’t allowed in the entire string.
        {8, 20} represents at least 8 characters and at most 20 characters.
         */

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the password is empty
        // return false
        if (password == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher m = p.matcher(password);

        // Return if the password
        // matched the ReGex
        return m.matches();
    }

    public static int diffMonths(Date start, Date end) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);

        Calendar endCal = new GregorianCalendar();
        endCal.setTime(end);

        int yearsInBetween = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
        int monthsDiff = endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH);
        long months = yearsInBetween * 12L + monthsDiff;
        //long age = yearsInBetween;
        return (int) months;
    }

    public static Date formatDate(String text, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            return formatter.parse(text);
        } catch (Exception e) { return null; }
    }

    public static String formatAmount(Double object) {
        if(object == null) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(object);
    }

    private static DecimalFormat df = new DecimalFormat("#,###");
    public static String formatLong(Long object) {
        if(object == null) {
            return "";
        }
        return df.format(object);
    }

    public static List<String> banks() {
        return Arrays.asList(
            "Access Bank",
            "Citibank",
            "Ecobank",
            "Fidelity Bank",
            "First Bank",
            "First City Monument Bank",
            "Globus Bank",
            "Guaranty Trust Bank",
            "Heritage Bank",
            "Key Stone Bank",
            "Polaris Bank",
            "Providus Bank",
            "Stanbic IBTC Bank",
            "Standard Chartered Bank Nigeria",
            "Sterling Bank",
            "SunTrust Bank",
            "Titan Trust Bank",
            "Union Bank",
            "United Bank For Africa",
            "Unity Bank",
            "Wema Bank",
            "Zenith Bank"
        );
    }

    public static Map<String, String> banksMap() {
        //Map<String, String> map = banks().stream().collect(Collectors.toMap(value -> value, value -> value));
        Map<String, String> map = AdAcctCrud.getBanks().stream().
                collect(Collectors.toMap(logo -> logo.filename, logo -> logo.title));
        return new TreeMap<>(map);
    }

    public static Html prettify(Html content) {
        return new Html(Jsoup.parse(content.body()).outerHtml());
    }

    public static String csrfToken(Http.Request request) {
        String token = "";
        Optional<CSRF.Token> optionalToken = CSRF.getToken(request);
        if(optionalToken.isPresent()) {
            token = optionalToken.get().value();
        }
        return token;
    }


    public static HttpURLConnection getConnection(String address, String body, String method, Map<String,String> headers) throws Exception {
        URL url = new URL(address);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }
        if(Util.isNotEmpty(body)){
            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        return con;
    }

    public static String getConnectionOutput(HttpURLConnection connection) throws Exception{
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader
                (connection.getInputStream(), "utf-8"))) {

            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

        }

        return response.toString();
    }

    public static String encodeAccountId(String accoutId, String email){
        String toCode = email +"_"+accoutId;
        return Base64.getEncoder().encodeToString(toCode.getBytes());
    }

    public static String decodeEncodedAccountId(String encoded){
        byte[] decodedBytes = Base64.getDecoder().decode(encoded);
        String decodedString = new String(decodedBytes);
        return decodedString;
    }

    public static String convert2Text(String text) {
        if(isNotEmpty(text)) {
            return text.replace("<br>", "\n")
                    .replace("<br/>", "\n")
                    .replace("<p>", "")
                    .replace("</p>", "");
        }
        return text;
    }

    public static String trim(String text) {
        if(isEmpty(text)) return "";
        text = text.replace('\u00A0',' ');
        if(isEmpty(text)) return text;
        return text.trim();
    }

    public static String rmBackSlash(String text) {
        if(isEmpty(text)) return "";
        return text.replace("\\", " ");
    }

    public static String getUri(Http.Request request) { return request.uri(); }


    public static String  getNoPageUriQuery(Http.Request request) {
        String uri = getUri(request);
        uri = uri.replaceAll("\\&page=\\d+", "").replaceAll("page=\\d+", "");
        String[] arr = uri.split("\\?");
        if(arr.length>1) {
            uri = "?" + arr[1];
            if(uri.endsWith("?")) {
                uri = uri.substring(0, uri.length() - 1);
            }
            return uri;
        }
        return "";
    }

    public static Map<String, String> getPriceRanges() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("500000-1000000", "500,000-N1,000,000");
        map.put("1000000-2000000", "1,000,000-N2,000,000");
        map.put("2000000-3000000", "2,000,000-N3,000,000");
        map.put("3000000-4000000", "3,000,000-N4,000,000");
        map.put("4000000-5000000", "4,000,000-N5,000,000");
        map.put("5000000-10000000", "5,000,000-N10,000,000");
        map.put("10000000-20000000", "10,000,000-N20,000,000");
        map.put("20000000-30000000", "20,000,000-N30,000,000");
        map.put("30000000-40000000", "30,000,000-N40,000,000");
        map.put("40000000-50000000", "40,000,000-N50,000,000");
        map.put("50000000-100000000", "50,000,000-N100,000,000");
        map.put("100000000-200000000", "100,000,000-N200,000,000");
        map.put("200000000-500000000", "200,000,000-N500,000,000");
        return map;
    }

    public static Map<String, String> getMileageRanges() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("0-10000", "10,000 km or less");
        map.put("10000-20000", "10,000 km to 20,000 km");
        map.put("20000-30000", "20,000 km to 30,000 km");
        map.put("30000-40000", "30,000 km to 40,000 km");
        map.put("40000-50000", "40,000 km to 50,000 km");
        map.put("50000-100000", "50,000 km to 100,000 km");
        map.put("100000-200000", "100,000 km to 200,000 km");
        map.put("200000-500000", "200,000 km to 500,000 km");
        return map;
    }

}
