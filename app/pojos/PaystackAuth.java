package pojos;

import lombok.Getter;
import lombok.Setter;

public class PaystackAuth {

    @Getter @Setter
    public String authorization_code;

    @Getter @Setter
    public Integer bin;

    @Getter @Setter
    public Integer last4;

    @Getter @Setter
    public Integer exp_month;

    @Getter @Setter
    public Integer exp_year;

    @Getter @Setter
    public String channel;

    @Getter @Setter
    public String card_type;

    @Getter @Setter
    public String bank;

    @Getter @Setter
    public String country_code;

    @Getter @Setter
    public String brand;

    @Getter @Setter
    public Boolean reusable;

    @Getter @Setter
    public String signature;

    @Getter @Setter
    public String account_name;

}
