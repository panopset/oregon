package com.panopset.compat;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlHelper {

  public static String getTextFromURL(final String urlStr) {
      URL url;
      try {
        url = new URL(urlStr);
      } catch (MalformedURLException e) {
        Logop.warn(String.format("Bad URL: %s, %s",urlStr, e.getMessage()));
        return "";
      }
      HttpGETclient client = new HttpGETclient(url);
      return client.getResponse();
  }
}
