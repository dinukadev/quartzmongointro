package com.quartz.mongo.intro.quartzintro.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.quartz.mongo.intro.quartzintro.constants.SystemProperties;

/**
 * This class configures the mongo client depending on the profile
 * 
 * @author dinuka
 *
 */
public class MongoConfig {

	/**
	 * This class defines the mongo client to be used with the development
	 * profile.
	 * 
	 * @author dinuka
	 *
	 */
	@Configuration
	@EnableTransactionManagement
	@Profile({ "dev" })
	public static class MongoConfigDev extends AbstractMongoConfiguration {

		@Autowired
		private Environment environment;

		@Override
		protected String getDatabaseName() {
			String serviceName = environment.getProperty(SystemProperties.SERVER_NAME);
			String environmentName = environment.getProperty(SystemProperties.ENVIRONMENT);
			return serviceName + "-" + environmentName;
		}

		@Override
		@Bean
		public Mongo mongo() throws Exception {
			String mongoURI = environment.getProperty(SystemProperties.MONGO_URI);
			String[] addresses = mongoURI.split(",");
			List<ServerAddress> servers = new ArrayList<>();
			for (String address : addresses) {
				String[] split = address.trim().split(":");
				servers.add(new ServerAddress(split[0].trim(), Integer.parseInt(split[1].trim())));
			}
			return new MongoClient(servers);
		}
	}

	/**
	 * This class defines the mongo client to be used with the production
	 * profile.
	 * 
	 * @author dinuka
	 *
	 */
	@Configuration
	@EnableTransactionManagement
	@Profile({ "prod" })
	public static class MongoConfigProd extends AbstractMongoConfiguration {

		@Autowired
		private Environment environment;

		@Override
		protected String getDatabaseName() {
			String serviceName = environment.getProperty(SystemProperties.SERVER_NAME);
			String environmentName = environment.getProperty(SystemProperties.ENVIRONMENT);
			return serviceName + "-" + environmentName;
		}

		@Bean
		public MongoClientOptions mongoClientOptions() {
			return MongoClientOptions.builder().sslEnabled(true).sslInvalidHostNameAllowed(true).build();
		}

		@Override
		@Bean
		public Mongo mongo() throws Exception {
			String dbPassword = environment.getProperty(SystemProperties.MONGO_PASSWORD);
			MongoCredential credential = MongoCredential.createCredential(
					environment.getProperty(SystemProperties.SERVER_NAME), getDatabaseName(), dbPassword.toCharArray());
			String mongoURI = environment.getProperty(SystemProperties.MONGO_URI);
			String[] addresses = mongoURI.split(",");
			List<ServerAddress> servers = new ArrayList<>();
			for (String address : addresses) {
				String[] split = address.trim().split(":");
				servers.add(new ServerAddress(split[0].trim(), Integer.parseInt(split[1].trim())));
			}
			return new MongoClient(servers, Arrays.asList(credential),
					MongoClientOptions.builder().sslEnabled(true).sslInvalidHostNameAllowed(true).build());
		}

	}

}
