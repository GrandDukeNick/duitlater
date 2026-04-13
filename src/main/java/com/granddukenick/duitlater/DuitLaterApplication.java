// DuitLaterApplication.java
package com.granddukenick.duitlater;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class DuitLaterApplication {
	public static void main(String[] args) {
		SpringApplication.run(DuitLaterApplication.class, args);
	}
}