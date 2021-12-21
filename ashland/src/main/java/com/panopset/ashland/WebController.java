package com.panopset.ashland;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

	@GetMapping({ "/", "/home", "/index", "/index.htm", "/index.html" })
	public String home(Model model, HttpServletResponse response, @RequestParam(required = false) String insec) {
		new VulnerableClass().processInsecureValue(insec);
		model.addAttribute("l4jfmnl", System.getProperty(Props.LOG4J_FORMAT_MSG_NO_LOOKUPS));
		model.addAttribute("logmgr", System.getProperty(Props.LOG_MANAGER));
		intentionallyLogHeader(response);
		return "index";
	}

	private void intentionallyLogHeader(HttpServletResponse response) {
		String acceptCharset = response.getHeader("Accept-Charset");
		new VulnerableClass().processInsecureValue(String.format("Accept-Charset value is %s", acceptCharset));
	}

}
