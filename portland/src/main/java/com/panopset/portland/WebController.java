package com.panopset.portland;

import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.panopset.compat.Logop;
import com.panopset.compat.Stringop;
import com.panopset.ophoneypot.AttackFilter;
import com.panopset.ophoneypot.AttackRecorderMysql;
import com.panopset.opspring.HttpHelper;

@Controller
public class WebController {

	private static final int FIVE_MINUTES = 1000 * 60 * 5;
	private static final String INDEX = "index";

	static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static {
		timeFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
	}

	@GetMapping({ "/", "/home", "/index", "/index.htm", "/index.html" })
	public String home(HttpServletRequest request, Model model, HttpServletResponse response,
			@RequestHeader HttpHeaders headers) {
		getFairlyRecentQuotes(model);
		lookForAttacksInHeaders(request, headers);
		return INDEX;
	}

	@PostMapping({ "/logon" })
	public String logon(HttpServletRequest request, Model model, HttpServletResponse response,
			@RequestParam(required = false) String userid, @RequestParam(required = false) String userpw,
			@RequestHeader HttpHeaders headers) {
		String msg = "Invalid password, to reset it, please contact your DropcashCM personal banker.";
		getFairlyRecentQuotes(model);
		model.addAttribute("msg", msg);
		lookForAttacksInHeaders(request, headers);
		lookForAttacksInRequestParam(request, "userid", userid);
		lookForAttacksInRequestParam(request, "userpw", userpw);
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

	private void lookForAttacksInHeaders(HttpServletRequest request, HttpHeaders headers) {
		for (Entry<String, List<String>> header : headers.entrySet()) {
			for (String headerValue : header.getValue()) {
				lookForAttacks(request, "header", header.getKey(), headerValue);
			}
		}
	}

	private void lookForAttacks(HttpServletRequest request, String category, String key, String value) {
		check(request, category, key, value);
	}

	public void check(HttpServletRequest request, String category, String key, String value) {
		if (isTheDroidsWereLookingFor(value)) {
			String thisHost;
			try {
				thisHost = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				thisHost = Stringop.toMaxLength(e.getMessage(), 32);
				Logop.error(e);
			}
			getAttackFilter().record(new HttpHelper().getClientIpAddress(request), thisHost, category, key, value);
		}
	}

	private boolean isTheDroidsWereLookingFor(String value) {
		return value.indexOf("jndi:") > -1;
	}

	private void lookForAttacksInRequestParam(HttpServletRequest request, String name, String value) {
		if (value == null) {
			return;
		}
		lookForAttacks(request, "param", name, value);
	}

	private AttackFilter attackFilter;

	private AttackFilter getAttackFilter() {
		if (attackFilter == null) {
			attackFilter = new AttackFilter(new AttackRecorderMysql());
		}
		return attackFilter;
	}
}
