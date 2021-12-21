package com.panopset.compat;

import java.util.regex.Pattern;

public class RegexValidator {

  private Pattern p;

  public RegexValidator(String regex) {
    if (Stringop.isPopulated(regex)) {
      p = Pattern.compile(regex);
    }
  }

  public boolean matches(String value) {
    if (value == null || p == null) {
      return false;
    }
    return p.matcher(value).find();
  }
}
