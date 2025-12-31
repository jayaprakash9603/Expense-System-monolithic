package com.jaya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.jaya.userservice",
        "com.jaya.expenseservice",
        "com.jaya.billservice",
        "com.jaya.categoryservice",
        "com.jaya.budgetservice",
        "com.jaya.paymentservice",
        "com.jaya.friendshipservice",
        "com.jaya.auditservice",
		"com.jaya.notificationservice",
		"com.jaya.analyticsservice"
})
@EnableJpaRepositories(basePackages = {
        "com.jaya.userservice.repository",
        "com.jaya.expenseservice.repository",
        "com.jaya.billservice.repository",
        "com.jaya.categoryservice.repository",
        "com.jaya.budgetservice.repository",
        "com.jaya.paymentservice.repository",
        "com.jaya.friendshipservice.repository",
        "com.jaya.auditservice.repository",
		"com.jaya.notificationservice.repository",
		"com.jaya.analyticsservice.repository"
})
public class ExpenseTrackingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseTrackingSystemApplication.class, args);
	}

}
