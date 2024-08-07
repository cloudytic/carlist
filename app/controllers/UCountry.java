package controllers;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import play.api.Play;
import services.Singletons;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.TreeSet;

public class UCountry implements Comparable<UCountry> {
    public String name;
    public String code;
    public String dial;

    public static UCountry[] countries;
    private static LinkedHashMap<String, UCountry> dialMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, String> selectMap = new LinkedHashMap<>();

    static {
        try {
            InputStream inputStream = Singletons.environment.resourceAsStream("countries.json");
            String result = IOUtils.toString(inputStream);
            Gson gson = new Gson();
            countries = gson.fromJson(result, UCountry[].class);
            TreeSet<UCountry> list = new TreeSet<>(Arrays.asList(countries));
            for(UCountry c: list) {
                dialMap.put(c.dial, c);
                selectMap.put(c.dial, c.name + "(" + c.dial + ")");
            }
        } catch (Exception e) {}
    }

    public static LinkedHashMap<String, UCountry> getCountries() {
        return dialMap;
    }

    public static LinkedHashMap<String, String> select() {
        return selectMap;
    }

    @Override
    public int compareTo(UCountry o) {
        return this.name.compareTo(o.name);
    }
}
