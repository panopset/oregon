package com.panopset.compat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class HttpClientAbstract {
	private int responseCode = 0;
	private String responseMessage = "";

	private final URL url;

	public final URL getURL() {
		return url;
	}

	protected abstract void

			setConnectionProperties(final HttpURLConnection connection);

	protected HttpClientAbstract(final URL newURL) {
		url = newURL;
	}

	protected HttpClientAbstract(final String urlString) {
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			Logop.error(e);
			throw new RuntimeException(e);
		}
	}

	private HttpURLConnection con;

	public final HttpURLConnection getConnection() throws IOException {
		if (con == null) {
			con = (HttpURLConnection) url.openConnection();
			setConnectionProperties(con);
		}
		return con;
	}

	protected final void closeConnection() {
		if (con != null) {
			try {
				responseCode = con.getResponseCode();
				responseMessage = con.getResponseMessage();
			} catch (IOException e) {
				Logop.error(e);
			}
			con.disconnect();
		}
	}

	public final int getResponseCode() {
		return responseCode;
	}
	
	public String getResponseMessage() {
		return responseMessage;
	}
}
