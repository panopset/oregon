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
		model.addAttribute("l4jfmnl", System.getProperty("log4j2.formatMsgNoLookups"));
		return "index";
	}

}
