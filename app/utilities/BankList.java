package utilities;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import controllers.AdAcctCrud;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BankList {

    private static List<AdAcctCrud.Logo> BANKS;

    public static List<AdAcctCrud.Logo> getBanks() {
        if (BANKS == null) {
            BANKS = new ArrayList<>();
            try {
                String json = logosJson();

                Gson gson = new Gson();
                Type type = new TypeToken<List<AdAcctCrud.Logo>>() {
                }.getType();
                List<AdAcctCrud.Logo> logos = gson.fromJson(json, type);

                for (AdAcctCrud.Logo logo : logos) {
                    List<String> category = logo.category;
                    for (String cat : category) {
                        if (cat.equals("Banking")) {
                            BANKS.add(logo);
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return BANKS;
    }




    public static Map<String,String> getBanksName() {
            Map<String,String> bankNames = new LinkedHashMap<>();
            bankNames.put("Access Bank Plc","Access Bank Plc");
            bankNames.put("Accion Microfinance Bank","Accion Microfinance Bank");
            bankNames.put("Citibank Nigeria Limited","Citibank Nigeria Limited");
            bankNames.put("Covenant Mirofinance Bank Ltd","Covenant Mirofinance Bank Ltd");
            bankNames.put("Ecobank Nigeria","Ecobank Nigeria");


        bankNames.put("Empire Trust Microfinance Bank","Empire Trust Microfinance Bank");
        bankNames.put("Fidelity Bank Plc","Fidelity Bank Plc");
        bankNames.put("Fina Trust Microfinance Bank","Fina Trust Microfinance Bank");
        bankNames.put("Finca Microfinance Bank Limited","Finca Microfinance Bank Limited");
        bankNames.put("First Bank of Nigeria Limited","First Bank of Nigeria Limited");
        bankNames.put("First City Monument Bank Limited","First City Monument Bank Limited");
        bankNames.put("Globus Bank Limited","Globus Bank Limited");
        bankNames.put("Guaranty Trust Holding Company Plc","Guaranty Trust Holding Company Plc");
        bankNames.put("Heritage Bank Plc","Heritage Bank Plc");
        bankNames.put("Infinity Microfinance Bank","Infinity Microfinance Bank");
        bankNames.put("Jaiz Bank Plc","Jaiz Bank Plc");
        bankNames.put("Keystone Bank Limited","Keystone Bank Limited");
        bankNames.put("Kuda Bank","Kuda Bank");
        bankNames.put("Lotus Bank","Lotus Bank");
        bankNames.put("Mint Finex MFB","Mint Finex MFB");
        bankNames.put("Mkobo MFB","Mkobo MFB");
        bankNames.put("Mutual Trust Microfinance Bank","Mutual Trust Microfinance Bank");
        bankNames.put("Parallex Bank Limited","Parallex Bank Limited");
        bankNames.put("Peace Microfinance Bank","Peace Microfinance Bank");
        bankNames.put("Pearl Microfinance Bank Limited","Pearl Microfinance Bank Limited");
        bankNames.put("Polaris Bank Limited","Polaris Bank Limited");
        bankNames.put("Providus Bank Limited","Providus Bank Limited");
        bankNames.put("Rephidim Microfinance Bank","Rephidim Microfinance Bank");
        bankNames.put("Rubies Bank","Rubies Bank");
        bankNames.put("Shepherd Trust Microfinance Bank","Shepherd Trust Microfinance Bank");
        bankNames.put("Sparkle Bank","Sparkle Bank");
        bankNames.put("Stanbic IBTC Bank Plc","Stanbic IBTC Bank Plc");
        bankNames.put("Standard Chartered","Standard Chartered");
        bankNames.put("Sterling Bank Plc","Sterling Bank Plc");
        bankNames.put("SunTrust Bank Nigeria Limited","SunTrust Bank Nigeria Limited");



        bankNames.put("TAJBank Limited","TAJBank Limited");
        bankNames.put("Titan Trust Bank Limited","Titan Trust Bank Limited");
        bankNames.put("Union Bank of Nigeria Plc","Union Bank of Nigeria Plc");
        bankNames.put("United Bank for Africa Plc","United Bank for Africa Plc");
        bankNames.put("Unity Bank Plc","Unity Bank Plc");
        bankNames.put("VFD MFB","VFD MFB");
        bankNames.put("Wema Bank Plc","Wema Bank Plc");
        bankNames.put("Zenith Bank Plc","Zenith Bank Plc");



        return bankNames;
    }


    public static String logosJson() {
        return "[\n" +
                "\t{\n" +
                "\t\t\"title\": \"Seedbuilders\",\n" +
                "\t\t\"filename\": \"seedbuilders\",\n" +
                "\t\t\"url\": \"https://seedbuildersng.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Coworking\",\n" +
                "\t\t\t\"Virtual Office\",\n" +
                "\t\t\t\"Foundation\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"BellaNaija\",\n" +
                "\t\t\"filename\": \"bellanaija\",\n" +
                "\t\t\"url\": \"https://www.bellanaija.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Media\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\n" +
                "\t{\n" +
                "\t\t\"title\": \"Paystack\",\n" +
                "\t\t\"filename\": \"paystack\",\n" +
                "\t\t\"url\": \"https://paystack.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"ALAT by Wema\",\n" +
                "\t\t\"filename\": \"alat_by_wema\",\n" +
                "\t\t\"url\": \"https://www.alat.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Mainstreet Bank\",\n" +
                "\t\t\"filename\": \"mainstreet_bank\",\n" +
                "\t\t\"url\": \"http://mainstreetcapltd.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Ekondo Microfinance Bank\",\n" +
                "\t\t\"filename\": \"ekondo_microfinance_bank\",\n" +
                "\t\t\"url\": \"http://www.ekondomfbank.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Aso Savings\",\n" +
                "\t\t\"filename\": \"aso_savings_loans\",\n" +
                "\t\t\"url\": \"http://www.asoplc.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"web boss\",\n" +
                "\t\t\"filename\": \"web_boss\",\n" +
                "\t\t\"url\": \"https://www.web-boss.com.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"EdTech\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Polaris Bank\",\n" +
                "\t\t\"filename\": \"polaris_bank\",\n" +
                "\t\t\"url\": \"https://www.polarisbanklimited.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"FourthCanvas\",\n" +
                "\t\t\"filename\": \"fourthcanvas\",\n" +
                "\t\t\"url\": \"https://fourthcanvas.co\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Branding\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Guaranty Trust Bank\",\n" +
                "\t\t\"filename\": \"guaranty_trust_bank\",\n" +
                "\t\t\"url\": \"https://www.gtbank.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Julius Berger\",\n" +
                "\t\t\"filename\": \"julius_berger\",\n" +
                "\t\t\"url\": \"https://www.julius-berger.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Construction\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Zenith Bank\",\n" +
                "\t\t\"filename\": \"zenith_bank\",\n" +
                "\t\t\"url\": \"https://www.zenithbank.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Cowrywise\",\n" +
                "\t\t\"filename\": \"cowrywise\",\n" +
                "\t\t\"url\": \"https://cowrywise.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Hotels.ng\",\n" +
                "\t\t\"filename\": \"hotels_ng\",\n" +
                "\t\t\"url\": \"https://hotels.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Real Estate\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"United Bank for Africa\",\n" +
                "\t\t\"filename\": \"united_bank_for_africa\",\n" +
                "\t\t\"url\": \"https://www.ubagroup.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Aero Contractors\",\n" +
                "\t\t\"filename\": \"aero_contractors\",\n" +
                "\t\t\"url\": \"http://www.flyaero.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Transportation\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Arik Air\",\n" +
                "\t\t\"filename\": \"arik_air\",\n" +
                "\t\t\"url\": \"https://www.arikair.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Transportation\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Konga\",\n" +
                "\t\t\"filename\": \"konga\",\n" +
                "\t\t\"url\": \"https://www.konga.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Ecommerce\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Peak Milk\",\n" +
                "\t\t\"filename\": \"peak_milk\",\n" +
                "\t\t\"url\": \"https://www.peakmilk.com.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"FMCG\",\n" +
                "\t\t\t\"Conglomerate\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Softcom\",\n" +
                "\t\t\"filename\": \"softcom\",\n" +
                "\t\t\"url\": \"https://softcom.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Bolt\",\n" +
                "\t\t\"filename\": \"bolt\",\n" +
                "\t\t\"url\": \"https://bolt.eu\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Transportation\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Nairabet\",\n" +
                "\t\t\"filename\": \"nairabet\",\n" +
                "\t\t\"url\": \"https://www.nairabet.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Gaming\",\n" +
                "\t\t\t\"Sports\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Betway\",\n" +
                "\t\t\"filename\": \"betway\",\n" +
                "\t\t\"url\": \"https://betway.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Gaming\",\n" +
                "\t\t\t\"Sports\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Paylater\",\n" +
                "\t\t\"filename\": \"paylater\",\n" +
                "\t\t\"url\": \"https://app.getcarbon.co/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Farmcrowdy\",\n" +
                "\t\t\"filename\": \"farmcrowdy\",\n" +
                "\t\t\"url\": \"https://www.farmcrowdy.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"FMCG\",\n" +
                "\t\t\t\"Conglomerate\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"MTN\",\n" +
                "\t\t\"filename\": \"mtn\",\n" +
                "\t\t\"url\": \"https://www.mtnonline.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Telecommunication\",\n" +
                "\t\t\t\"ISP\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Opera\",\n" +
                "\t\t\"filename\": \"opera\",\n" +
                "\t\t\"url\": \"https://www.opera.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Iroko TV\",\n" +
                "\t\t\"filename\": \"iroko_tv\",\n" +
                "\t\t\"url\": \"https://irokotv.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Media\",\n" +
                "\t\t\t\"Entertainment\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Reliance HMO\",\n" +
                "\t\t\"filename\": \"reliance_hmo\",\n" +
                "\t\t\"url\": \"https://www.reliancehmo.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Health\",\n" +
                "\t\t\t\"NGO\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Ren Money\",\n" +
                "\t\t\"filename\": \"ren_money\",\n" +
                "\t\t\"url\": \"https://renmoney.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Branch\",\n" +
                "\t\t\"filename\": \"branch\",\n" +
                "\t\t\"url\": \"https://branch.com.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"PiggyVest\",\n" +
                "\t\t\"filename\": \"piggyvest\",\n" +
                "\t\t\"url\": \"https://www.piggyvest.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"GUO Transport\",\n" +
                "\t\t\"filename\": \"guo_transport\",\n" +
                "\t\t\"url\": \"https://guotransport.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Transportation\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"GIGM\",\n" +
                "\t\t\"filename\": \"gigm\",\n" +
                "\t\t\"url\": \"https://gigm.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Transportation\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"AXA Mansard\",\n" +
                "\t\t\"filename\": \"axa_mansard\",\n" +
                "\t\t\"url\": \"https://www.axamansard.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Flutterwave\",\n" +
                "\t\t\"filename\": \"flutterwave\",\n" +
                "\t\t\"url\": \"https://flutterwave.com/ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Smile\",\n" +
                "\t\t\"filename\": \"smile\",\n" +
                "\t\t\"url\": \"https://smile.com.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Telecommunication\",\n" +
                "\t\t\t\"ISP\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"SportyBet\",\n" +
                "\t\t\"filename\": \"sportybet\",\n" +
                "\t\t\"url\": \"https://www.sportybet.com/ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Gaming\",\n" +
                "\t\t\t\"Sports\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Terra Kulture\",\n" +
                "\t\t\"filename\": \"terra_kulture\",\n" +
                "\t\t\"url\": \"https://www.terrakulture.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Media\",\n" +
                "\t\t\t\"Entertainment\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Verifi\",\n" +
                "\t\t\"filename\": \"verifi\",\n" +
                "\t\t\"url\": \"https://www.verifi.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Aella Credit\",\n" +
                "\t\t\"filename\": \"aella_credit\",\n" +
                "\t\t\"url\": \"https://aellacredit.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"BuyCoins\",\n" +
                "\t\t\"filename\": \"buycoins\",\n" +
                "\t\t\"url\": \"https://buycoins.africa/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Cryptocurrency\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"PoFela Nigeria\",\n" +
                "\t\t\"filename\": \"pofela\",\n" +
                "\t\t\"url\": \"https://www.pofela.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Cryptocurrency\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Kadarko.com Marketplace\",\n" +
                "\t\t\"filename\": \"kadarko\",\n" +
                "\t\t\"url\": \"http://storesby.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Ecommerce\",\n" +
                "\t\t\t\"Marketplace\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Barter\",\n" +
                "\t\t\"filename\": \"barter\",\n" +
                "\t\t\"url\": \"https://barter.flutterwave.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Arla Food Inc.\",\n" +
                "\t\t\"filename\": \"arla\",\n" +
                "\t\t\"url\": \"https://www.arla.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"FMCG\",\n" +
                "\t\t\t\"Conglomerate\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Bet9ja\",\n" +
                "\t\t\"filename\": \"bet9ja\",\n" +
                "\t\t\"url\": \"https://www.bet9ja.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Gaming\",\n" +
                "\t\t\t\"Sports\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Dano Milk\",\n" +
                "\t\t\"filename\": \"dano\",\n" +
                "\t\t\"url\": \"https://dano.com.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"FMCG\",\n" +
                "\t\t\t\"Conglomerate\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"First City Monument Bank Ltd\",\n" +
                "\t\t\"filename\": \"fcmb\",\n" +
                "\t\t\"url\": \"https://www.fcmb.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Milo\",\n" +
                "\t\t\"filename\": \"milo\",\n" +
                "\t\t\"url\": \"https://www.nestle.com/brands/allbrands/milo\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"FMCG\",\n" +
                "\t\t\t\"Conglomerate\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Nestle\",\n" +
                "\t\t\"filename\": \"nestle\",\n" +
                "\t\t\"url\": \"https://www.nestle.com\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"FMCG\",\n" +
                "\t\t\t\"Conglomerate\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Paga\",\n" +
                "\t\t\"filename\": \"paga\",\n" +
                "\t\t\"url\": \"https://www.mypaga.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Sterling Bank Plc\",\n" +
                "\t\t\"filename\": \"sterling_bank\",\n" +
                "\t\t\"url\": \"https://sterling.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"TeamApt\",\n" +
                "\t\t\"filename\": \"teamapt\",\n" +
                "\t\t\"url\": \"https://teamapt.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Union Bank Nigeria\",\n" +
                "\t\t\"filename\": \"union_bank\",\n" +
                "\t\t\"url\": \"https://www.unionbankng.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"eHealth Africa\",\n" +
                "\t\t\"filename\": \"ehealth_africa\",\n" +
                "\t\t\"url\": \"https://www.ehealthafrica.org/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Health\",\n" +
                "\t\t\t\"NGO\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Check DC\",\n" +
                "\t\t\"filename\": \"check_dc\",\n" +
                "\t\t\"url\": \"https://check-dc.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Branding\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Mobnia\",\n" +
                "\t\t\"filename\": \"mobnia\",\n" +
                "\t\t\"url\": \"https://mobnia.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Branding\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Health Assur Ltd\",\n" +
                "\t\t\"filename\": \"health_assur\",\n" +
                "\t\t\"url\": \"https://www.healthassur.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Health\",\n" +
                "\t\t\t\"NGO\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"SiriusLabs\",\n" +
                "\t\t\"filename\": \"siriuslabs\",\n" +
                "\t\t\"url\": \"https://siriuslabs.io/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Product Design\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"How Do You Tech\",\n" +
                "\t\t\"filename\": \"how_do_you_tech\",\n" +
                "\t\t\"url\": \"https://howdoyou.tech/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"EdTech\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Quidax\",\n" +
                "\t\t\"filename\": \"quidax\",\n" +
                "\t\t\"url\": \"https://www.quidax.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Cryptocurrency\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Fliqpay\",\n" +
                "\t\t\"filename\": \"fliqpay\",\n" +
                "\t\t\"url\": \"https://www.fliqpay.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Cryptocurrency\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Access Bank PLC\",\n" +
                "\t\t\"filename\": \"access_bank\",\n" +
                "\t\t\"url\": \"https://www.accessbankplc.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Ecobank\",\n" +
                "\t\t\"filename\": \"ecobank\",\n" +
                "\t\t\"url\": \"https://ecobank.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Fidelity Bank Nigeria\",\n" +
                "\t\t\"filename\": \"fidelity_bank\",\n" +
                "\t\t\"url\": \"https://www.fidelitybank.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"First Bank Nigeria\",\n" +
                "\t\t\"filename\": \"first_bank\",\n" +
                "\t\t\"url\": \"https://www.firstbanknigeria.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Standard Chartered\",\n" +
                "\t\t\"filename\": \"standard_chartered\",\n" +
                "\t\t\"url\": \"https://www.sc.com/ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Nigerian Breweries PLC\",\n" +
                "\t\t\"filename\": \"nigerian_breweries\",\n" +
                "\t\t\"url\": \"https://nbplc.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"FMCG\",\n" +
                "\t\t\t\"Conglomerate\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Vconnect\",\n" +
                "\t\t\"filename\": \"vconnect\",\n" +
                "\t\t\"url\": \"https://m.vconnect.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Marketplace\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Stanbic IBTC Bank\",\n" +
                "\t\t\"filename\": \"stanbic_ibtc\",\n" +
                "\t\t\"url\": \"https://www.stanbicibtcbank.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Wema Bank\",\n" +
                "\t\t\"filename\": \"wema_bank\",\n" +
                "\t\t\"url\": \"https://www.wemabank.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Heritage Bank PLC\",\n" +
                "\t\t\"filename\": \"heritage_bank\",\n" +
                "\t\t\"url\": \"https://www.hbng.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Keystone Bank Limited\",\n" +
                "\t\t\"filename\": \"keystone_bank\",\n" +
                "\t\t\"url\": \"https://www.keystonebankng.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Interswitch\",\n" +
                "\t\t\"filename\": \"interswitch\",\n" +
                "\t\t\"url\": \"https://www.interswitchgroup.com/ug\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Verve\",\n" +
                "\t\t\"filename\": \"verve\",\n" +
                "\t\t\"url\": \"https://www.myverveworld.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Quickteller\",\n" +
                "\t\t\"filename\": \"quickteller\",\n" +
                "\t\t\"url\": \"https://www.quickteller.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Nixero Enterprises\",\n" +
                "\t\t\"filename\": \"nixero_enterprises\",\n" +
                "\t\t\"url\": \"https://www.nixeroict.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Korapay\",\n" +
                "\t\t\"filename\": \"korapay\",\n" +
                "\t\t\"url\": \"https://korapay.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Andela Nigeria\",\n" +
                "\t\t\"filename\": \"andela\",\n" +
                "\t\t\"url\": \"https://andela.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Airtel Nigeria\",\n" +
                "\t\t\"filename\": \"airtel\",\n" +
                "\t\t\"url\": \"https://www.airtel.com.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Telecommunication\",\n" +
                "\t\t\t\"ISP\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"9mobile\",\n" +
                "\t\t\"filename\": \"9mobile\",\n" +
                "\t\t\"url\": \"https://9mobile.com.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Telecommunication\",\n" +
                "\t\t\t\"ISP\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Globacom Limited\",\n" +
                "\t\t\"filename\": \"glo\",\n" +
                "\t\t\"url\": \"https://www.gloworld.com/ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Telecommunication\",\n" +
                "\t\t\t\"ISP\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Innovation Growth Hub\",\n" +
                "\t\t\"filename\": \"innovation_growth_hub\",\n" +
                "\t\t\"url\": \"https://ighub.com.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Gigalayer\",\n" +
                "\t\t\"filename\": \"gigalayer\",\n" +
                "\t\t\"url\": \"https://gigalayer.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Business Day\",\n" +
                "\t\t\"filename\": \"business_day\",\n" +
                "\t\t\"url\": \"https://businessday.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Newspaper\",\n" +
                "\t\t\t\"Blog\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Circa\",\n" +
                "\t\t\"filename\": \"circa\",\n" +
                "\t\t\"url\": \"https://circa-lagos.business.site/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Restaurant\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Disrupt Africa\",\n" +
                "\t\t\"filename\": \"disrupt_africa\",\n" +
                "\t\t\"url\": \"http://disrupt-africa.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Newspaper\",\n" +
                "\t\t\t\"Blog\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Federal Inland Revenue Service\",\n" +
                "\t\t\"filename\": \"firs\",\n" +
                "\t\t\"url\": \"https://www.firs.gov.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Government Agency\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Investment One\",\n" +
                "\t\t\"filename\": \"investment_one\",\n" +
                "\t\t\"url\": \"https://www.investment-one.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Nigeria Football Federation\",\n" +
                "\t\t\"filename\": \"nff\",\n" +
                "\t\t\"url\": \"https://www.nigeriaff.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Government Agency\",\n" +
                "\t\t\t\"Sports\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"National Identity Management Commission\",\n" +
                "\t\t\"filename\": \"nimc\",\n" +
                "\t\t\"url\": \"https://www.nimc.gov.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Government Agency\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Oando PLC\",\n" +
                "\t\t\"filename\": \"oando\",\n" +
                "\t\t\"url\": \"https://www.oandoplc.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Petroleum and Gas\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"ThankUCash\",\n" +
                "\t\t\"filename\": \"thank_u_cash\",\n" +
                "\t\t\"url\": \"http://thankucash.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"The Tony Elumelu Foundation\",\n" +
                "\t\t\"filename\": \"tony_elumelu_foundation\",\n" +
                "\t\t\"url\": \"https://www.tonyelumelufoundation.org/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Foundation\",\n" +
                "\t\t\t\"NGO\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Total Nigeria\",\n" +
                "\t\t\"filename\": \"total\",\n" +
                "\t\t\"url\": \"https://www.total.com.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Petroleum and Gas\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Wallets Africa\",\n" +
                "\t\t\"filename\": \"wallets_africa\",\n" +
                "\t\t\"url\": \"https://wallets.africa/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Banking\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Wilson’s Juice Co\",\n" +
                "\t\t\"filename\": \"wilsons\",\n" +
                "\t\t\"url\": \"http://www.wilsonsjuiceco.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"FMCG\",\n" +
                "\t\t\t\"Conglomerate\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Workstation\",\n" +
                "\t\t\"filename\": \"workstation\",\n" +
                "\t\t\"url\": \"https://www.workstationng.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Coworking\",\n" +
                "\t\t\t\"Virtual Office\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Verdant\",\n" +
                "\t\t\"filename\": \"verdant\",\n" +
                "\t\t\"url\": \"https://verdant.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Agritech\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"OkadaBooks\",\n" +
                "\t\t\"filename\": \"okadabooks\",\n" +
                "\t\t\"url\": \"https://okadabooks.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Publishing\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Hall 7 Real Estate\",\n" +
                "\t\t\"filename\": \"hall_7_real_estate\",\n" +
                "\t\t\"url\": \"https://www.hall7projects.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Construction\",\n" +
                "\t\t\t\"Real Estate\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"ntel\",\n" +
                "\t\t\"filename\": \"ntel\",\n" +
                "\t\t\"url\": \"http://www.ntel.com.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Telecommunication\",\n" +
                "\t\t\t\"ISP\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Tecno\",\n" +
                "\t\t\"filename\": \"tecno\",\n" +
                "\t\t\"url\": \"https://www.tecno-mobile.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Decagon\",\n" +
                "\t\t\"filename\": \"decagon\",\n" +
                "\t\t\"url\": \"https://decagonhq.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Spectranet\",\n" +
                "\t\t\"filename\": \"spectranet\",\n" +
                "\t\t\"url\": \"https://spectranet.com.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"ISP\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"TechCity\",\n" +
                "\t\t\"filename\": \"techcity\",\n" +
                "\t\t\"url\": \"https://www.techcityng.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Blog\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Slot\",\n" +
                "\t\t\"filename\": \"slot\",\n" +
                "\t\t\"url\": \"https://slot.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Marketplace\",\n" +
                "\t\t\t\"Ecommerce\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Ventures Platform\",\n" +
                "\t\t\"filename\": \"ventures_platform\",\n" +
                "\t\t\"url\": \"https://venturesplatform.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Financial Services\",\n" +
                "\t\t\t\"Foundation\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Printivo\",\n" +
                "\t\t\"filename\": \"printivo\",\n" +
                "\t\t\"url\": \"https://printivo.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Ecommerce\",\n" +
                "\t\t\t\"Branding\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Spinlet\",\n" +
                "\t\t\"filename\": \"spinlet\",\n" +
                "\t\t\"url\": \"https://spinlet.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Media\",\n" +
                "\t\t\t\"Entertainment\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"ipNX\",\n" +
                "\t\t\"filename\": \"ipnx\",\n" +
                "\t\t\"url\": \"https://www.ipnxnigeria.net/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"ISP\",\n" +
                "\t\t\t\"Telecommunication\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Whogohost\",\n" +
                "\t\t\"filename\": \"whogohost\",\n" +
                "\t\t\"url\": \"https://www.whogohost.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Web Host\",\n" +
                "\t\t\t\"Domain Registrar\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"TechCabal\",\n" +
                "\t\t\"filename\": \"techcabal\",\n" +
                "\t\t\"url\": \"https://techcabal.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Media\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Glamafric\",\n" +
                "\t\t\"filename\": \"glamafric\",\n" +
                "\t\t\"url\": \"http://glamafric.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Marketplace\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Lendsqr\",\n" +
                "\t\t\"filename\": \"lendsqr\",\n" +
                "\t\t\"url\": \"https://lendsqr.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Mercurie\",\n" +
                "\t\t\"filename\": \"mercurie\",\n" +
                "\t\t\"url\": \"https://mercurie.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Digital Marketing\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Eyowo\",\n" +
                "\t\t\"filename\": \"eyowo\",\n" +
                "\t\t\"url\": \"https://www.eyowo.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Gloopro\",\n" +
                "\t\t\"filename\": \"gloopro\",\n" +
                "\t\t\"url\": \"https://www.gloopro.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"OnePipe\",\n" +
                "\t\t\"filename\": \"onepipe\",\n" +
                "\t\t\"url\": \"https://www.onepipe.io/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Ariga\",\n" +
                "\t\t\"filename\": \"ariga\",\n" +
                "\t\t\"url\": \"https://www.ariga.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Marketplace\",\n" +
                "\t\t\t\"Ecommerce\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Fieldinsight\",\n" +
                "\t\t\"filename\": \"fieldinsight\",\n" +
                "\t\t\"url\": \"https://www.fieldinsight.co/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"The Guardian\",\n" +
                "\t\t\"filename\": \"the_guardian\",\n" +
                "\t\t\"url\": \"https://m.guardian.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Newspaper\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Africa Fintech Foundry\",\n" +
                "\t\t\"filename\": \"africa_fintech_foundry\",\n" +
                "\t\t\"url\": \"https://africafintechfoundry.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Virtual Office\",\n" +
                "\t\t\t\"Fintech\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Cool FM\",\n" +
                "\t\t\"filename\": \"cool_fm\",\n" +
                "\t\t\"url\": \"https://coolfm.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Radio\",\n" +
                "\t\t\t\"Broadcasting\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Lightbulb UX Laboratory\",\n" +
                "\t\t\"filename\": \"lightbulbux\",\n" +
                "\t\t\"url\": \"https://lightbulbux.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"UX Design\",\n" +
                "\t\t\t\"Product Design\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"First Securities Discount House Limited\",\n" +
                "\t\t\"filename\": \"fsdh\",\n" +
                "\t\t\"url\": \"https://fsdhgroup.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Merchant Bank\",\n" +
                "\t\t\t\"Banking\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Busha\",\n" +
                "\t\t\"filename\": \"busha\",\n" +
                "\t\t\"url\": \"https://busha.co/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Cryptocurrency\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"ProduqtEdge\",\n" +
                "\t\t\"filename\": \"produqtedge\",\n" +
                "\t\t\"url\": \"https://www.produqtedge.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Product Management\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Omenka\",\n" +
                "\t\t\"filename\": \"omenka\",\n" +
                "\t\t\"url\": \"https://www.omenkagallery.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Art Gallery\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Thrive Agric\",\n" +
                "\t\t\"filename\": \"thrive_agric\",\n" +
                "\t\t\"url\": \"http://www.thriveagric.com/\",\n" +
                "\t\t\"category\": [\"Agritech\"]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Schoolable\",\n" +
                "\t\t\"filename\": \"schoolable\",\n" +
                "\t\t\"url\": \"https://schoolable.co/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Financial Services\",\n" +
                "\t\t\t\"EdTech\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"SeamPay\",\n" +
                "\t\t\"filename\": \"seampay\",\n" +
                "\t\t\"url\": \"https://www.seampay.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Summitech Computing Limited\",\n" +
                "\t\t\"filename\": \"summitech\",\n" +
                "\t\t\"url\": \"https://www.summitech.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Abeg\",\n" +
                "\t\t\"filename\": \"abeg\",\n" +
                "\t\t\"url\": \"https://www.abeg.app/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Air Peace\",\n" +
                "\t\t\"filename\": \"air_peace\",\n" +
                "\t\t\"url\": \"https://www.flyairpeace.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Transportation\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Buypower\",\n" +
                "\t\t\"filename\": \"buypower\",\n" +
                "\t\t\"url\": \"https://buypower.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Power Distribution\",\n" +
                "\t\t\t\"ICT\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Filmhouse Cinemas\",\n" +
                "\t\t\"filename\": \"filmhouse\",\n" +
                "\t\t\"url\": \"https://filmhouseng.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Entertainment\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Ibom Air\",\n" +
                "\t\t\"filename\": \"ibom_air\",\n" +
                "\t\t\"url\": \"https://www.ibomair.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Transportation\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Kuda Bank\",\n" +
                "\t\t\"filename\": \"kuda_bank\",\n" +
                "\t\t\"url\": \"https://kudabank.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"UPS\",\n" +
                "\t\t\"filename\": \"ups\",\n" +
                "\t\t\"url\": \"https://www.ups.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Logistics\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Silverbird Cinemas\",\n" +
                "\t\t\"filename\": \"silverbird_cinemas\",\n" +
                "\t\t\"url\": \"https://silverbirdcinemas.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Entertainment\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Seller\",\n" +
                "\t\t\"filename\": \"seller\",\n" +
                "\t\t\"url\": \"https://seller.ng/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Ecommerce\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Yellowcard\",\n" +
                "\t\t\"filename\": \"yellowcard\",\n" +
                "\t\t\"url\": \"https://yellowcard.io/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Cryptocurrency\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Cravvings\",\n" +
                "\t\t\"filename\": \"cravvings\",\n" +
                "\t\t\"url\": \"https://www.cravvings.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Local Search\",\n" +
                "\t\t\t\"Business Ratings and Reviews\",\n" +
                "\t\t\t\"Online Food Delivery\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Pettysave\",\n" +
                "\t\t\"filename\": \"pettysave\",\n" +
                "\t\t\"url\": \"https://www.pettysave.com/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Financial Services\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"DearDesigner\",\n" +
                "\t\t\"filename\": \"dear_designer\",\n" +
                "\t\t\"url\": \"https://deardesigner.xyz/\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"EdTech\",\n" +
                "\t\t\t\"Software\",\n" +
                "\t\t\t\"Product Design\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Peace Mass Transit\",\n" +
                "\t\t\"filename\": \"peace_mass_transit\",\n" +
                "\t\t\"url\": \"https://pmt.ng/\",\n" +
                "\t\t\"category\": [\"Transportation\", \"Logistics\"]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Patricia\",\n" +
                "\t\t\"filename\": \"patricia\",\n" +
                "\t\t\"url\": \"https://patricia.com.ng/\",\n" +
                "\t\t\"category\": [\"Ecommerce\", \"Financial Services\"]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Fundall\",\n" +
                "\t\t\"filename\": \"fundall\",\n" +
                "\t\t\"url\": \"https://www.fundall.io/\",\n" +
                "\t\t\"category\": [\"Banking\", \"Financial Services\"]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Startuplist Africa\",\n" +
                "\t\t\"filename\": \"startuplist_africa\",\n" +
                "\t\t\"url\": \"https://startuplist.africa\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Big Data\",\n" +
                "\t\t\t\"Business Intelligence\",\n" +
                "\t\t\t\"Analytics\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Mono\",\n" +
                "\t\t\"filename\": \"mono\",\n" +
                "\t\t\"url\": \"https://mono.co\",\n" +
                "\t\t\"category\": [\n" +
                "\t\t\t\"Financial Data\",\n" +
                "\t\t\t\"Software\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Tix Africa\",\n" +
                "\t\t\"filename\": \"tix_africa\",\n" +
                "\t\t\"url\": \"https://tix.africa\",\n" +
                "\t\t\"category\": [\"Event Management\"]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"gidimo\",\n" +
                "\t\t\"filename\": \"gidimo\",\n" +
                "\t\t\"url\": \"https://gidimo.com/\",\n" +
                "\t\t\"category\": [\"EdTech\"]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"title\": \"Thepeer\",\n" +
                "\t\t\"filename\": \"thepeer\",\n" +
                "\t\t\"url\": \"https://thepeer.co\",\n" +
                "\t\t\"category\": [\"Financial Services\"]\n" +
                "\t}\n" +
                "]\n";
    }
}
