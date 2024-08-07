package pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.Util;
import controllers.routes;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class SellerContact {
    public String name;
    public String phone;
    public String logo;
    public Long autoCount;


    @JsonIgnore
    private static final String[] alpha = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
    @JsonIgnore
    private static final Map<String, Integer> map = new LinkedHashMap<>();
    static {
        int i = 0;
        for(String ch: alpha) {
            map.put(ch, i);
            i++;
        }
    }

    @JsonIgnore
    public String url() {
        String code = getCode(phone);
        return url(code);
    }

    @JsonIgnore
    public static String url(String code) {
        if("error".equals(code)) {
            return "#";
        } else {
            return routes.ListingController.seller(code).toString();
        }
    }

    @JsonIgnore
    public static String getCode(String phone) {
        if(Util.isEmpty(phone) || !phone.matches("\\d+")) {
            //System.out.println(phone);
            return "error";
        }
        String[] array = phone.split("");
        StringBuilder code = new StringBuilder();
        for(String n: array) {
            code.append(alpha[Integer.parseInt(n)]);
        }
        return code.toString();
    }

    @JsonIgnore
    public static String getPhone(String code) {
        String[] array = code.split("");
        StringBuilder phone = new StringBuilder();
        for(String a: array) {
            phone.append(map.get(a));
        }
        return phone.toString();
    }
}
