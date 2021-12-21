package com.panopset.compat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * HTTP GET client.
 *
 */
public class HttpGETclient extends HttpClientAbstract {

	/**
	 * @param urlString URL.
	 */
	public HttpGETclient(final String urlString) {
		super(urlString);
	}

	/**
	 * @param url URL.
	 */
	public HttpGETclient(final URL url) {
		super(url);
	}

	/**
	 *
	 * @return Get the text from a file represented by a URL in a List, one array
	 *         entry for each line found.
	 */
	public List<String> getResponseList() {
		List<String> rtn = new ArrayList<String>();
		String s = null;
		try (InputStreamReader isr = new InputStreamReader(getConnection().getInputStream());
				BufferedReader br = new BufferedReader(isr)) {
			while ((s = br.readLine()) != null) {
				rtn.add(s);
			}
		} catch (FileNotFoundException e) {
			Logop.warn(String.format("File not found: %s, %s", getURL().toString(), e.getMessage()));
		} catch (IOException ex) {
			Logop.warn(String.format("Error getting response: %s, %s", getURL().toString(), ex.getMessage()));
		}
		closeConnection();
		return rtn;
	}

	public String getResponse() {
		StringWriter sw = new StringWriter();
		for (String s : getResponseList()) {
			sw.append(s).append(Stringop.getEol());
		}
		return sw.toString();
	}

	@Override
	protected void setConnectionProperties(final HttpURLConnection con) {
		try {
			con.setRequestMethod("GET");
			con.setConnectTimeout(getTimeout());
		} catch (ProtocolException ex) {
			Logop.error(ex);
		}
	}

	/**
	 * Timeout in milliseconds.
	 */
	private Integer timeout;

	/**
	 *
	 * @return Timeout in milliseconds.
	 */
	public int getTimeout() {
		if (timeout == null) {
			timeout = 5000;
		}
		return timeout;
	}

	/**
	 *
	 * @param millis New timout in milliseconds.
	 */
	public void setTimeout(final int millis) {
		timeout = millis;
	}
}
