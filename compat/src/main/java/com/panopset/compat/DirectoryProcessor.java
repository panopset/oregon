package com.panopset.compat;

import java.io.File;

public class DirectoryProcessor {

  private final File dir;
  private final Listener ltr;
  private final boolean isRecursive;

  public DirectoryProcessor(File dir, Listener listener) {
    this(dir, listener, true);
  }

  public DirectoryProcessor(File dir, Listener listener, boolean recursive) {
    this.dir = dir;
    ltr = listener;
    isRecursive = recursive;
  }

  public interface Listener {
    boolean processFile(final File file);
  }

  public void exec() {
    exec(dir.listFiles());
  }
  
  public void exec(File... files) {
    if (files == null) {
      return;
    }
    for (File f : files) {
      if (f.isDirectory()) {
        if (isRecursive) {
          new DirectoryProcessor(f, ltr).exec();
        }
      } else {
        if (ltr.processFile(f)) {
          Logop.green(String.format(Stringop.CS, Nls.xlate("Processed"), Fileop.getCanonicalPath(f)));
        }
      }
    }
  }
}
