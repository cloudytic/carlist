package services;

import java.util.ArrayList;
import java.util.List;

public class PidCalculator {
    private static final String[] alpha = { "A", "B", "C", "D", "E", "F", "G",
            "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

    private static final List<String> alphabetsList = new ArrayList<>();
    static {
        for(int i = 0; i < alpha.length; i++) {
            for(int j = 0; j < alpha.length; j++) {
                for(int k = 0; k < alpha.length; k++) {
                    for(int l = 0; l < alpha.length; l++) {
                        alphabetsList.add("P"+alpha[i]+alpha[j]+alpha[k]+alpha[l]);
                    }
                }
            }
        }
    }

    private static final Long CENT = 10L;
    private static final int CENT_L = 1;

    public static String getRef(Long id) {
        Long remainder = id%CENT;

        String number = String.valueOf(remainder);

        long index = id/CENT;

        int  i = (int) index;
        return  number + alphabetsList.get(i);
    }

    public static Long getId(String ref) {
        String alpha = ref.substring(CENT_L);
        int index = alphabetsList.indexOf(alpha);
        String numb = ref.replace(alpha, "");
        long number = Long.parseLong(numb);
        return  number + index * CENT;
    }

    public static void main(String[] args) {
        //System.out.println(getPid(0L));
        //System.out.println(getPid(3_317_759L));

        System.out.println(getId("6PYPFM"));
    }
}
