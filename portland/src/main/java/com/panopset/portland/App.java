package com.panopset.portland;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

	public static void main(String... args) {
		if (args != null && args.length > 2) {
			System.setProperty("PDBURL", args[0]);
			System.setProperty("PDBUSR", args[1]);
			System.setProperty("PDBPWD", args[2]);
		}
		SpringApplication.run(App.class, args);
	}
}
