package com.panopset.compat;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Stringop {

	private Stringop() {
	}

	private static String lineTerminator;

	/**
	 * StandardCharsets.UTF_8.name();
	 */
	public static final String UTF_8 = "UTF-8";
	public static final String TAB = "\t";
	public static final String JAVA_RTN = "\n";
	public static final String COPYRIGHT = "©";
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final int LINE_FEED_VALUE = 10;
	public static final String LINE_FEED = "\n";
	public static final int CARRIAGE_RETURN_VALUE = 13;
	public static final String CARRIAGE_RETURN = "\r";
	public static final String DOS_RTN = "\r\n";
	public static final String USER_HOME = System.getProperty("user.home");
	public static final String FILE_SEP = System.getProperty("file.separator");
	public static final String PATH_SEP = System.getProperty("path.separator");
	public static final String USER_DIR = System.getProperty("user.dir");
	public static final String TEMP_DIR_PATH = USER_HOME + FILE_SEP + "tmp";
	public static final String EOL = System.getProperty("line.separator");
	public static final String LAST_MODIFIED_FORMAT = "EEEE, MMMM dd, yyyy, h:mm a(zz)";
	public static final String TEST = "test";
	public static final String FOO = "foo";
	public static final String BAR = "bar";
	public static final String BAT = "bat";
	public static final String CS = "%s:%s";

	public static boolean isRegexFound(String regex, String value) {
		return new RegexValidator(regex).matches(value);
	}

	/**
	 * Check for match.
	 * 
	 * @param s1 String one.
	 * @param s2 String two.
	 * @param r1 Return value if one = two.
	 * @param r2 Return value if one &lt;&gt; two.
	 * @return r1 if s1 = s2, otherwise r2.
	 */
	public static String check4match(final String s1, final String s2, final String r1, final String r2) {
		if (s1 == null || s2 == null || r1 == null || r2 == null) {
			return "";
		}
		if (s1.equals(s2)) {
			return r1;
		}
		return r2;
	}

	public static String capund(final String str) {
		if (str != null && str.length() > 0) {
			String rtn = str.replaceAll("()([A-Z])", "$1_$2").toUpperCase();
			if (rtn.charAt(0) == '_') {
				rtn = rtn.substring(1);
			}
			return rtn;
		}
		return "";
	}

	public static String capitalize(final String str) {
		if (str != null && str.length() > 0) {
			if (str.length() > 1) {
				return ("" + str.charAt(0)).toUpperCase() + str.substring(1);
			}
			return str.toUpperCase();
		}
		return "";
	}

	public static String toUpperCase(final String value) {
		return value.toUpperCase();
	}

	public static String toLowerCase(final String value) {
		return value == null ? "" : value.toLowerCase();
	}

	public static boolean isPopulated(String str) {
		return !isEmpty(str);
	}

	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}

	public static String pullAfter(String str, String searchStr) {
		if (str == null || searchStr == null) {
			return "";
		}
		if (str.length() < searchStr.length()) {
			return "";
		}
		int i = str.indexOf(searchStr);
		if (i == -1) {
			return "";
		}
		return str.substring(i + searchStr.length());
	}

	public static String getEol() {
		if (lineTerminator == null) {
			lineTerminator = "\n";
		}
		return lineTerminator;
	}

	public static void setEol(final String value) {
		lineTerminator = value;
	}

	public static String wrapFixedWidth(final String str, final int width) {
		if (!isPopulated(str)) {
			return "";
		}
		if (width < 1) {
			return str;
		}
		if (str.length() < width) {
			return str;
		}
		StringWriter sw = new StringWriter();
		for (int i = 0; i < str.length(); i++) {
			sw.append(str.charAt(i));
			if ((i + 1) % width == 0) {
				sw.append(getEol());
			}
		}
		return sw.toString();
	}

	public static int countReturns(final String str) {
		if (str == null) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '\n') {
				count++;
			}
		}
		return count;
	}

	/**
	 * All trailing empty Strings are removed.
	 *
	 * @param sa String array.
	 * @return String array that ends with the last populated sa entry.
	 */
	public static String[] trimTrailingEmptyLines(final String[] sa) {
		if (sa == null) {
			return new String[] {};
		}
		int jdx = sa.length;
		while (jdx > 0 && !isPopulated(sa[jdx - 1])) {
			jdx = jdx - 1;
		}
		if (jdx == sa.length) {
			return sa;
		}
		String[] result = new String[jdx];
		System.arraycopy(sa, 0, result, 0, result.length);
		return result;
	}

	/**
	 * Empty String is returned if args is null. Otherwise all non-null element in
	 * arg is concatenated in order.
	 *
	 * @param args Strings to join.
	 * @return Concatenated args in one String.
	 */
	public static String concat(final String... args) {
		return concat(Arrays.asList(args));
	}

	public static String concat(final List<String> pts) {
		if (pts == null) {
			return "";
		}
		StringWriter sw = new StringWriter();
		for (String s : pts) {
			if (s != null) {
				sw.append(s);
			}
		}
		return sw.toString();
	}

	public static List<String> arrayToList(final String[] strs) {
		List<String> rtn = new ArrayList<>();
		if (strs == null) {
			return rtn;
		}
		Collections.addAll(rtn, strs);
		return rtn;
	}

	public static List<String> stringToList(final String str) {
		List<String> rtn = new ArrayList<>();
		if (str == null) {
			return rtn;
		}
		return Streamop.getLinesFromReader(new StringReader(str));
	}

	public static List<String> stringToListWithEol(final String str) {
		List<String> rtn = new ArrayList<>();
		if (str == null) {
			return rtn;
		}
		return Streamop.getLinesFromReaderWithEol(new StringReader(str));
	}

	public static String getDollarString(long pennies) {
		return NumberFormat.getCurrencyInstance().format((new BigDecimal(pennies)).setScale(2, RoundingMode.HALF_UP)
				.divide(new BigDecimal(100), RoundingMode.HALF_UP));
	}

	public static int parseInt(String value) {
		if (!isPopulated(value)) {
			return 0;
		} else {
			return parseInt(value, 0);
		}
	}

	public static int parseInt(String value, Integer defaultValue) {
		if (isPopulated(value)) {
			String str = value.replace(",", "");
			try {
				if (value.charAt(0) == '+') {
					// Pre-Java 7, you have to take out the + sign.
					return Integer.parseInt(str.substring(1));
				} else {
					return Integer.parseInt(str);
				}
			} catch (NumberFormatException nfe) {
				Logop.error(value);
				Logop.error(nfe.getMessage());
				return defaultValue == null ? 0 : defaultValue;
			}
		} else {
			return defaultValue == null ? 0 : defaultValue;
		}
	}

	public static boolean isNumber(String value) {
		return value.matches("[0-9]*");
	}

	public static boolean isInteger(String value) {
		return value.matches("-?\\d+");
	}

	public static boolean isBlank(String value) {
		return value == null || "".contentEquals(value.trim());
	}

	public static String appendEol(String value) {
		return String.format("%s%s", value, Stringop.getEol());
	}

	public static String notNull(String value) {
		if (value == null) {
			return "";
		}
		return value;
	}

	public static String randomString() {
		return String.format("%d", Randomop.nextLong());
	}

	private static final AtomicLong idCounter = new AtomicLong();

	public static long getNextJvmUniqueID() {
		return idCounter.getAndIncrement();
	}

	public static String getNextJvmUniqueIDstr() {
		return String.format("%d", getNextJvmUniqueID());
	}

	public static boolean parseBoolean(String value, boolean defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if ("on".contentEquals(value.toLowerCase())) {
			return true;
		}
		if ("yes".contentEquals(value.toLowerCase())) {
			return true;
		}
		if ("y".contentEquals(value.toLowerCase())) {
			return true;
		}
		return Boolean.parseBoolean(value);
	}

	public static boolean parseBoolean(String value) {
		return parseBoolean(value, false);
	}

	public static boolean skipComment(String value) {
		if (!isPopulated(value)) {
			return true;
		}
		if ("#".equals(value.substring(0, 1))) {
			return true;
		}
		return value.length() > 1 && "//".equals(value.substring(0, 2));
	}

	public static String listToString(List<String> list) {
		StringWriter sw = new StringWriter();
		for (String s : list) {
			sw.append(s);
			sw.append(getEol());
		}
		return sw.toString();
	}

	public static String replace(String source, String target, String replacement) {
		return source.replace(target, replacement);
	}

	public static String replaceAll(String source, String target, String replacement) {
		return source.replaceAll(target, replacement);
	}

	public static List<String> replaceFirstLinePreserveIndentation(List<String> source, String lineToReplaceContaining,
			String fullReplacementLine) {
		boolean firstTime = true;
		String priorIndentation = "";
		List<String> rtn = new ArrayList<>();
		for (String str : source) {
			if (firstTime) {
				if (str.contains(lineToReplaceContaining)) {
					firstTime = false;
					rtn.add(String.format("%s%s", priorIndentation, fullReplacementLine));
				} else {
					rtn.add(str);
					priorIndentation = getIndentation(str);
				}
			} else {
				rtn.add(str);
			}
		}
		return rtn;
	}

	public static String getIndentation(String str) {
		if (str == null) {
			return "";
		}
		StringWriter sw = new StringWriter();
		for (int i = 0; i < str.length(); i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				sw.append(str.charAt(i));
			} else {
				return sw.toString();
			}
		}
		return sw.toString();
	}

	public static String appendFilePath(String... pathElements) {
		StringWriter sw = new StringWriter();
		boolean firstTime = true;
		for (String pathElement : pathElements) {
			if (firstTime) {
				sw.append(pathElement);
				firstTime = false;
			} else {
				if (pathElement.indexOf(Stringop.FILE_SEP) != 0) {
					sw.append(Stringop.FILE_SEP);
				}
				sw.append(pathElement);
			}
		}
		return sw.toString();
	}

	public static String firstHexDiff(String expected, String value) {
		if (value == null) {
			return "value is null";
		}
		if (expected == null) {
			return "expected is null";
		}
		if (value.length() == 0) {
			return "value is empty";
		}
		if (expected.length() == 0) {
			return "expected is empty";
		}
		System.out.println("Comparing expected, to value:");
		System.out.println(expected);
		System.out.println(value);
		if (value.length() != expected.length()) {
			return String.format("Expected length is %d and value length is %d", expected.length(), value.length());
		}
		for (int i = 0; i < value.length(); i++) {
			char c0 = expected.charAt(i);
			char c1 = value.charAt(i);
			if (c0 == c1) {
			} else {
				return String.format("At position %d, expected %s, but was %s", i, String.format("%04x", (int) c0),
						String.format("%04x", (int) c1));
			}
		}
		return "values are identical";
	}
}
