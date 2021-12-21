package com.panopset.compat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {
  private final File targetFile;

  public FileProcessor(File file) {
    targetFile = file;
  }

  private final List<LineFilter> lineFilters = new ArrayList<>();

  public FileProcessor withLineFilter(LineFilter lf) {
    lineFilters.add(lf);
    return this;
  }

  public boolean exec() {
    return new Report(targetFile, lineFilters).exec();
  }
}
