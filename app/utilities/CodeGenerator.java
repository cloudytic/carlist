package utilities;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
    private static final String[] alpha = { "A", "B", "C", "D", "E", "F", "G",
            "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };
    private static final List<String> alphabetsList = new ArrayList<>();
    static {
        for (String first: alpha) {
            for (String second: alpha) {
                for (String third: alpha) {
                    alphabetsList.add(first + second + third);
                }
            }
        }
    }

    private static final Long TOTAL = 13824L;
    //This code generator has a limit of 191,102,975 for ZZZZZZ
    public static String getCode(Long id) {
        int remainder = (int) (id% TOTAL);
        int index = (int) (id/ TOTAL);
        return  alphabetsList.get(remainder) + alphabetsList.get(index);
    }

    public static Long getId(String code) {
        String alpha1 = code.substring(0,3);
        int index1 = alphabetsList.indexOf(alpha1);
        String alpha2 = code.substring(3,6);
        int index2 = alphabetsList.indexOf(alpha2);
        return  index1 + index2 * TOTAL;
    }

    //System.out.println(ReferralController.getCode(0L));
    //System.out.println(ReferralController.getId("ZZZZZZ"));
}
