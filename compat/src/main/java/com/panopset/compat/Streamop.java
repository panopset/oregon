package com.panopset.compat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Streamop {

  private Streamop() {}

  public static String getTextFromStream(final InputStream is) {
    if (is == null) {
      Logop.error("null inputstream");
      return "";
    }
    try (BufferedInputStream bis = new BufferedInputStream(is)) {
      StringWriter sw = new StringWriter();
      int inp = bis.read();
      while (inp != -1) {
        sw.append((char) inp);
        inp = bis.read();
      }
      return sw.toString();
    } catch (IOException ex) {
      Logop.error(ex);
      return "";
    }
  }

  public static void copyChars(final Reader reader, final Writer writer) throws IOException {
    try (BufferedReader br = new BufferedReader(reader);
        BufferedWriter bw = new BufferedWriter(writer)) {
      String line;
      while ((line = br.readLine()) != null) {
        bw.write(line);
        bw.write(Stringop.getEol());
      }
    }
  }

  public static void copyStream(final InputStream inputStream, final OutputStream outputStream)
      throws IOException {
    if (outputStream == null) {
      return;
    }
    try (BufferedInputStream is = new BufferedInputStream(inputStream);
        BufferedOutputStream os = new BufferedOutputStream(outputStream)) {
      int byt;
      while ((byt = is.read()) != -1) {
        os.write(byt);
      }
    }
  }

  public static void streamToWriter(final InputStream is, Writer w) throws IOException {
    try (BufferedInputStream bis = new BufferedInputStream(is);
        BufferedWriter bw = new BufferedWriter(w)) {
      int byt;
      while ((byt = is.read()) != -1) {
        bw.write(byt);
      }
    }
  }

  public static List<String> getLinesFromReader(Reader reader) {
    List<String> rtn = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(reader)) {
      String line = br.readLine();
      while (line != null) {
        rtn.add(line);
        line = br.readLine();
      }
    } catch (IOException ex) {
      Logop.error(ex);
    }
    return rtn;
  }

  public static List<String> getLinesFromReaderWithEol(Reader reader) {
    List<String> rtn = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(reader)) {
      String line = br.readLine();
      while (line != null) {
        rtn.add(String.format("%s%s", line, Stringop.getEol()));
        line = br.readLine();
      }
    } catch (IOException ex) {
      Logop.error(ex);
    }
    return rtn;
  }
}
