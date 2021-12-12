package com.panopset.ashland;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

	public static void main(String... args) {

		// Turns out this makes no difference:
		// System.setProperty(Props.LOG_MANAGER, "org.apache.logging.log4j.jul.LogManager");

		SpringApplication.run(App.class, args);
	}
}
