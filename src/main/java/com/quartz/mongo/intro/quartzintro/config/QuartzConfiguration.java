package com.quartz.mongo.intro.quartzintro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * This class will configure and setup quartz using the
 * {@link SchedulerFactoryBean}
 * 
 * @author dinuka
 *
 */
@Configuration
public class QuartzConfiguration {

	/**
	 * Here we integrate quartz with Spring and let Spring manage initializing
	 * quartz as a spring bean.
	 * 
	 * @return an instance of {@link SchedulerFactoryBean} which will be managed
	 *         by spring.
	 */
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
		scheduler.setApplicationContextSchedulerContextKey("applicationContext");
		scheduler.setConfigLocation(new ClassPathResource("quartz.properties"));
		scheduler.setWaitForJobsToCompleteOnShutdown(true);
		return scheduler;
	}

}