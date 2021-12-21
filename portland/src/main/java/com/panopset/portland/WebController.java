package com.panopset.portland;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.panopset.compat.Logop;

@Controller
public class WebController {

	private static final int FIVE_MINUTES = 1000 * 60 * 5;
	private static final String INDEX = "index";
	
	
	static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static {
		timeFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
	}
	
	@GetMapping({ "/", "/home", "/index", "/index.htm", "/index.html" })
	public String home(Model model, HttpServletResponse response, @RequestHeader(value = "Accept-Charset", required = false) String acceptCharsetHeaderValue) {
		getFairlyRecentQuotes(model);
		String logAttackHeader = String.format("Accept-Charset: %s", acceptCharsetHeaderValue); 
		Logop.info(logAttackHeader);
		return INDEX;
	}

	@GetMapping({ "/logon" })
	public String logon(Model model, HttpServletResponse response, @RequestParam(required = false) String userid, @RequestParam(required = false) String userpw) {
		String msg = "Invalid password, to reset it, please contact your DropcashCM personal banker.";
		getFairlyRecentQuotes(model);
		model.addAttribute("msg", msg);
		return INDEX;
	}

	private String getDateStamp() {
		return timeFormat.format(new Date());
	}

	private long lastUpdate = 0L;

	private String frontPageNews;


	private void getFairlyRecentQuotes(Model model) {
		StringWriter sw = new StringWriter();
		Long time = new Date().getTime();
		if (frontPageNews == null || time - FIVE_MINUTES > lastUpdate) {	
			String dow;
			String bc;
			lastUpdate = time;
			dow = new StockQuote().getDowFromGoogle();
			bc = new StockQuote().getBcFromGoogle();
			sw.append("The dow was at ");
			sw.append(dow);
			sw.append(", and bitcoin was at ");
			sw.append(bc);
			sw.append(", as of ");
			sw.append(getDateStamp());
			sw.append(" New York time");
			frontPageNews = sw.toString();
		}
		model.addAttribute("downow", frontPageNews);
	}
	
	private static final String NA = "Not available at this time.";
}
