package com.panopset.compat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Fileop {

  private Fileop() {}

  private static final String NULL_FILE_MSG = "file is null";

  public static File getUserHome() {
    return new File(System.getProperty("user.home"));
  }

  public static void touch(File file) throws IOException {
    if (prepFileForWriting(file) && !file.createNewFile()) {
      Logop.warn(String.format("Unable to create %s", Fileop.getCanonicalPath(file)));
    }
  }

  public static void deleteLines(File file, final String searchCriteria) {
    if (file == null) {
      Logop.error(NULL_FILE_MSG);
      return;
    }
    Logop.green(String.format("Found file: %s", Fileop.getCanonicalPath(file)));
    if (file.isDirectory()) {
      DirectoryProcessor.Listener listener = new DirectoryProcessor.Listener() {

        @Override
        public boolean processFile(File file) {
          return deleteLinesInFile(file, searchCriteria);
        }
      };
      new DirectoryProcessor(file, listener).exec();
    } else {
      deleteLinesInFile(file, searchCriteria);
      Logop.green(
          String.format(Stringop.CS, Nls.xlate("Processed file"), Fileop.getCanonicalPath(file)));
    }
  }

  private static boolean deleteLinesInFile(File file, String searchCriteria) {
    if (searchCriteria == null) {
      return false;
    }
    final List<String> targets = Stringop.stringToList(searchCriteria);
    if (targets.isEmpty()) {
      return false;
    }
    new FileProcessor(file).withLineFilter(new LineFilter() {
      @Override
      public String filter(String str) {
        for (String s : targets) {
          if (str.contains(s)) {
            return null;
          }
        }
        return str;
      }
    }).exec();
    return true;
  }

  public static String getStandardPath(File file) {
    return getCanonicalPath(file).replace("\\", "/");
  }

  public static String getParentDirectory(final File file) {
    if (file.exists()) {
      File fullFile = new File(getCanonicalPath(file));
      return getCanonicalPath(fullFile.getParentFile());
    }
    return getCanonicalPath(file.getParentFile());
  }

  public static boolean fileExists(File file) {
    if (file == null) {
      return false;
    } else {
      return file.exists() && file.isFile() && file.canRead();
    }
  }
  public static boolean dirExists(File dir) {
    if (dir == null) {
      return false;
    } else {
      return dir.exists() && dir.isDirectory() && dir.canRead();
    }
  }

  public static File createTempFile(String fileName) {
    return new File(combinePaths(Stringop.TEMP_DIR_PATH, fileName));
  }

  public static void write(String[] strs, File file) {
    if (!prepFileForWriting(file)) {
      return;
    }
    try (FileWriter fw = new FileWriter(file);

        BufferedWriter bw = new BufferedWriter(fw)) {
      for (String s : strs) {
        bw.write(s);
        bw.write(Stringop.EOL);
      }
    } catch(IOException ex) {
      Logop.error(ex);
    }
  }

  public static void write(List<String> strs, File file) {
    if (!prepFileForWriting(file)) {
      return;
    }
    try (FileWriter fw = new FileWriter(file);

        BufferedWriter bw = new BufferedWriter(fw)) {
      for (String s : strs) {
        bw.write(s);
        bw.write(Stringop.EOL);
      }
    } catch(IOException ex) {
      Logop.error(ex);
    }
  }

  public static void write(String str, File file) {
    if (!prepFileForWriting(file)) {
      return;
    }
    try (FileWriter fw = new FileWriter(file);

        BufferedWriter bw = new BufferedWriter(fw)) {
      bw.write(str);
    } catch (IOException ex) {
      Logop.error(ex);
    }
  }

  private static boolean prepFileForWriting(File file) {
    if (file == null) {
      Logop.error("Can't write to a null file");
      return false;
    }
    if (Stringop.isBlank(file.getPath())) {
      Logop.error("Can't write to blank file path.");
      return false;
    }
    File parentFile = file.getParentFile();
    if (parentFile == null) {
      Logop.error("Unable to write to file with no parent directory.");
      return false;
    }
    file.getParentFile().mkdirs();
    return true;
  }

  public static String getCanonicalPath(File file) {
    if (file == null) {
      return "";
    }
    if (Stringop.isBlank(file.getPath())) {
      return "";
    }
    try {
      return file.getCanonicalPath();
    } catch (IOException ex) {
      Logop.error(ex);
      return ex.getMessage();
    }
  }

  public static boolean isFileOneOfExtensions(File file, String extsToTry) {
    if (extsToTry != null && file != null) {
      String fileExtension = getExtension(file);
      if (extsToTry.contains(",")) {
        String[] exts = extsToTry.split(",");
        for (String ext : exts) {
          if (ext.trim().equalsIgnoreCase(fileExtension)) {
            return true;
          }
        }
        return false;
      } else {
        return getExtension(file).equalsIgnoreCase(extsToTry);
      }
    } else {
      return false;
    }
  }

  public static String getExtension(String fileName) {
    int i = fileName.lastIndexOf(46);
    return i > 0 ? fileName.substring(i + 1) : "";
  }

  public static String getExtension(File file) {
    return getExtension(file.getName());
  }

  public static String combinePaths(String dirPath, String path) {
    return combinePaths(new File(dirPath), path);
  }

  public static String combinePaths(File dir, String path) {
    return String.format("%s%s%s", getCanonicalPath(dir), FILE_SEP, path);
  }

  public static String readTextFile(String filePath) throws IOException {
    return readTextFile(new File(filePath));
  }

  public static String readTextFile(File file) {
    if (!file.exists()) {
      Logop.warn(String.format("File %s doesn't exist.", getCanonicalPath(file)));
      return "";
    }
    StringWriter sw = new StringWriter();
    try {
      Streamop.copyChars(new FileReader(file), sw);
    } catch (IOException ex) {
      Logop.error(file, ex);
    }
    return sw.toString();
  }

  public static List<String> readLines(File file) {
    if (!file.exists()) {
      Logop.error("File does not exist", file);
      return new ArrayList<>();
    }
    try {
      return Streamop.getLinesFromReader(new FileReader(file));
    } catch (FileNotFoundException ex) {
      Logop.error(ex);
      return new ArrayList<>();
    }
  }

  public static void delete(final File file) {
    if (file == null) {
      return;
    }
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (File f : files) {
          delete(f);
        }
      }
    }
    if (file.exists()) {
      try {
        Files.delete(file.toPath());
      } catch (IOException e) {
        Logop.error(e);
      }
    }
  }

  public static void mkdirs(final File path) {
    if (!path.mkdirs()) {
      Logop.error(
          String.format("%s %s", Nls.xlate("Unable to create path to"), getCanonicalPath(path)));
    }
  }

  public static void copyInputStreamToFile(final InputStream inp, String fileName)
      throws IOException {
    copyInputStreamToFile(inp, new File(fileName));
  }

  public static void copyInputStreamToFile(final InputStream is, File file) throws IOException {
    Streamop.streamToWriter(is, new FileWriter(file));
  }

  public static void moveFile(File ff, File tf) throws IOException {
    Files.move(ff.toPath(), tf.toPath());
  }

  public static void copyFile(File ff, File tf) throws IOException {
    Files.copy(ff.toPath(), tf.toPath());
  }

  public static final File TEMP_DIRECTORY = new File(Stringop.USER_HOME + "/temp");
  public static final String CRLF = "\r\n";
  public static final String FILE_SEP = System.getProperty("file.separator");
  public static final String PATH_SEP = System.getProperty("path.separator");

  public static String removeExtension(String str) {
    int i = str.lastIndexOf(".");
    if (i > -1) {
      return str.substring(0, i);
    }
    return str;
  }

  public static boolean checkParent(File targetFile) {
    File parent = targetFile.getParentFile();
    if (parent.exists()) {
      return true;
    } else {
      return parent.mkdirs();
    }
  }
}
