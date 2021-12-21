package com.panopset.compat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Report {
  private final File ff;
  private final File tf;
  private final List<LineFilter> lfs = new ArrayList<>();

  public Report(File file, List<LineFilter> lineFilters) {
    this(file, file, lineFilters);
  }

  public Report(File file, LineFilter lineFilter) {
    this(file, file);
    lfs.add(lineFilter);
  }

  public Report(File fromFile, File toFile, LineFilter lineFilter) {
    this(fromFile, toFile);
    lfs.add(lineFilter);
  }

  public Report(File fromFile, File toFile, List<LineFilter> lineFilters) {
    this(fromFile, toFile);
    lfs.addAll(lineFilters);
  }

  public Report(File fromFile, File toFile) {
    ff = fromFile;
    tf = toFile;
  }

  public boolean exec() {
    if (tf == null || tf.equals(ff)) {
      return execInMemory();
    } else {
      return execToFile();
    }
  }

  private boolean execInMemory() {
    try (StringWriter sw = new StringWriter()) {
      boolean hasChanged = processToWriter(sw);
      Fileop.write(sw.toString(), ff);
      return hasChanged;
    } catch (IOException e) {
      Logop.error(e);
      return false;
    }
  }

  private boolean execToFile() {
    try (FileWriter fw = new FileWriter(tf)) {
      return processToWriter(fw);
    } catch (IOException e) {
      Logop.error(tf, e);
      return false;
    }
  }

  private boolean processToWriter(Writer writer) {
    boolean changed = false;
    try (FileReader fr = new FileReader(ff);
        BufferedReader br = new BufferedReader(fr);
        BufferedWriter bw = new BufferedWriter(writer)) {
      String str = br.readLine();
      while (str != null) {
        String filteredStr = str;
        for (LineFilter lf : lfs) {
          filteredStr = lf.filter(filteredStr);
        }
        if (filteredStr != null) {
          bw.write(filteredStr);
          bw.write(Stringop.getEol());
        }
        if (!str.equals(filteredStr)) {
          changed = true;
        }
        str = br.readLine();
      }
    } catch (IOException ex) {
      Logop.error(ff, ex);
    }
    return changed;
  }
}
