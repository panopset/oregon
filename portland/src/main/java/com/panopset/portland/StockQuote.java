package com.panopset.portland;

import com.panopset.compat.Stringop;
import com.panopset.compat.UrlHelper;

public class StockQuote {

    private static String PFX = "<div class=\"YMlKec\">";
    private static String PFXBC = "data-source=\"BTC\" data-target=\"USD\" data-last-price=\"";
    private static String SFX = "</div>";
    private static String SFXBC = "\" data-last-normal-market-timestamp=";
    public static void main(String... args) {
        System.out.println(new StockQuote().getDowFromGoogle());
        System.out.println(new StockQuote().getBcFromGoogle());
    }

    String getDowFromGoogle() {
        String rawData = UrlHelper.getTextFromURL("https://www.google.com/finance/quote/.DJI:INDEXDJX");
        for (String line : Stringop.stringToList(rawData)) {
            int pfx = line.indexOf(PFX);
            if (pfx > -1) {
                String str = line.substring(pfx);
                int sfx = str.indexOf(SFX);
                if (sfx > -1) {
                   return str.substring(PFX.length(), sfx);
                }
            }
        }
        return "Market data not available at this time.";
    }

    String getBcFromGoogle() {
        String rawData = UrlHelper.getTextFromURL("https://www.google.com/finance/quote/BTC-USD?hl=en");
        for (String line : Stringop.stringToList(rawData)) {
            int pfx = line.indexOf(PFXBC);
            if (pfx > -1) {
                String str = line.substring(pfx);
                int sfx = str.indexOf(SFXBC);
                if (sfx > -1) {
                   return str.substring(PFXBC.length(), sfx);
                }
            }
        }
        return "Market data not available at this time.";
    }
}
