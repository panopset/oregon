package com.panopset.compat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public enum Logop {

  INSTANCE;


  private final Logger logger = Logger.getGlobal();

  private boolean isDebugging;

  private final List<Listener> listeners = new ArrayList<>();

  public static Deque<LogEntry> getStack() {
    return INSTANCE.stack;
  }

  /**
   * Keeping this around, because of the System/38 &amp; AS/400 DSPMSG CLP command. Identical to
   * info(msg).
   * 
   * @param msg Message to log at Level.INFO.
   */
  public static void dspmsg(String msg) {
    info(msg);
  }

  public static void info(String msg) {
    INSTANCE.report(new LogEntry(Alert.GREEN, Level.INFO, audit(msg)));
  }

  public static void warn(String msg) {
    INSTANCE.report(new LogEntry(Alert.YELLOW, Level.WARNING, audit(msg)));
  }

  public static void debug(String msg) {
    INSTANCE.report(new LogEntry(Alert.ORANGE, Level.FINE, audit(msg)));
  }

  public static void error(String msg) {
    INSTANCE.report(new LogEntry(Alert.RED, Level.SEVERE, audit(msg)));
  }

  public static void warn(Exception ex) {
    warn(ex.getMessage());
  }

  public static void error(Exception ex) {
    INSTANCE.handleException(ex);
  }

  public static void error(String msg, Exception ex) {
    error(msg);
    INSTANCE.handleException(ex);
  }

  public static void green(String msg) {
    dspmsg(msg);
  }

  public static void addLogListener(Listener listener) {
    INSTANCE.listeners.add(listener);
  }

  public static void handle(Exception e) {
    INSTANCE.handleException(e);
  }

  private void handleException(Exception ex) {
    getLogger().log(Level.SEVERE, ex.getMessage(), ex);
    ex.printStackTrace();
    LogEntry logEntry = new LogEntry(Alert.RED, Level.SEVERE, ex.getMessage());
    for (Listener listener : INSTANCE.listeners) {
      listener.log(logEntry);
    }
    logalog(logEntry);
  }

  private void report(LogEntry logRecord) {
    getLogger().log(logRecord.getLevel(), logRecord.getMessage());
    for (Listener listener : INSTANCE.listeners) {
      listener.log(logRecord);
    }
    logalog(logRecord);
  }

  private void clearListeners() {
    for (Listener listener : INSTANCE.listeners) {
      listener.log(new LogEntry(Alert.GREEN, Level.INFO, ""));
    }
  }

  public static Logger getLogger() {
    return INSTANCE.logger;
  }

  public interface Listener {
    void log(LogEntry logEntry);
  }

  private static final SimpleDateFormat timestampFormat =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ~ ");

  private String printHistory() {
    StringWriter sw = new StringWriter();
    for (LogEntry lr : stack) {
      sw.append(timestampFormat.format(lr.getTimestamp()));
      sw.append(lr.getMessage());
      sw.append("\n");
    }
    return sw.toString();
  }

  private final Deque<LogEntry> stack = new ConcurrentLinkedDeque<>();

  public static void error(String message, File file) {
    error(String.format("%s: %s", message, Fileop.getCanonicalPath(file)));
  }

  public static String getStackTrace(Throwable throwable) throws IOException {
    try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
      throwable.printStackTrace(pw);
      pw.flush();
      return sw.toString();
    }
  }

  public static String getStackTraceAndCauses(Throwable throwable) throws IOException {
    StringWriter sw = new StringWriter();
    sw.append("See log");
    sw.append(": ");
    sw.append(throwable.getMessage());
    sw.append(Stringop.EOL);
    sw.append("*************************");
    sw.append(Stringop.EOL);
    sw.append(getStackTrace(throwable));
    sw.append(Stringop.EOL);

    for (Throwable cause = throwable.getCause(); cause != null; cause = cause.getCause()) {
      sw.append("*************************");
      sw.append(Stringop.EOL);
      sw.append(getStackTrace(cause));
      sw.append(Stringop.EOL);
    }

    return sw.toString();
  }

  private void logalog(LogEntry logEntry) {
    if (stack.size() > 999) {
      for (int i = 0; i < 100; i++) {
        stack.removeFirst();
      }
    }
    stack.add(logEntry);
  }

  public static void turnOnDebugging() {
    INSTANCE.isDebugging = true;
  }

  public static void turnOffDebugging() {
    INSTANCE.isDebugging = false;
  }

  public static String getEntryStackAsText() {
    return INSTANCE.printHistory();
  }

  public static boolean isDebugging() {
    return INSTANCE.isDebugging;
  }

  public static void clear() {
    getStack().clear();
    turnOffDebugging();
    INSTANCE.clearListeners();
  }

  public static void error(File file, IOException ex) {
    info(Fileop.getCanonicalPath(file));
    error(ex);
  }

  private static String audit(String msg) {
    return msg;
  }

  public enum Alert {
    PURPLE, BLUE, RED, ORANGE, YELLOW, GREEN
  }

  public static class LogEntry {

    public LogEntry(Alert alert, Level level, String message) {
      this.alert = alert;
      this.level = level;
      this.message = message;
      this.timestamp = new Date();
    }

    @Override
    public String toString() {
      return String.format("%s %s %s", getMessage(), getAlert().toString(),
          timestampFormat.format(getTimestamp()));
    }

    private Level level;

    public Level getLevel() {
      if (level == null) {
        level = Level.ALL;
      }
      return level;
    }

    private Alert alert;

    public Alert getAlert() {
      if (alert == null) {
        alert = Alert.GREEN;
      }
      return alert;
    }

    private Date timestamp;

    public Date getTimestamp() {
      if (timestamp == null) {
        timestamp = new Date();
      }
      return timestamp;
    }

    private String message;

    public String getMessage() {
      if (message == null) {
        message = "";
      }
      return message;
    }
  }

  public static void provideLogLocation() {
    try {
      String tempDir = System.getProperty("java.io.tmpdir");
      String logFileName = "panopset.log";
      String logFilePath = Stringop.appendFilePath(tempDir, logFileName);
      File file = new File(logFilePath);
      Handler fh = new FileHandler(logFilePath);
      fh.setFormatter(new SimpleFormatter());
      Logger.getLogger("").addHandler(fh);
      Logger.getLogger("com.panopset").setLevel(Level.WARNING);
      Logop.green(String.format("Detailed logs can be found in %s", Fileop.getCanonicalPath(file)));
    } catch (IOException e) {
      Logop.error(e);
    }
    
//    LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
//    FileAppender appender = (FileAppender) config.getAppender("file");
//    File file = new File(appender.getFileName());
//    Logop.green(String.format("Detailed logs can be found in %s", Fileop.getCanonicalPath(file)));
  }
}
