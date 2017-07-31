package com.quartz.mongo.intro.quartzintro.scheduler;

import org.apache.commons.lang3.StringUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import com.novemberain.quartz.mongodb.MongoDBJobStore;
import com.quartz.mongo.intro.quartzintro.constants.SchedulerConstants;
import com.quartz.mongo.intro.quartzintro.constants.SystemProperties;

/**
 * 
 * <p>
 * We extend the {@link MongoDBJobStore} because we need to set the custom mongo
 * db parameters. Some of the configuration comes from system properties set via
 * docker and the others come via the application.yml files we have for each
 * environment.
 * </p>
 * 
 * < These are set as part of initialization. This class is initialized by
 * {@link StdSchedulerFactory} and defined in the quartz.properties file.
 * 
 * </p>
 * 
 * @author dinuka
 *
 */
public class CustomMongoQuartzSchedulerJobStore extends MongoDBJobStore {

	private static String mongoAddresses;
	private static String userName;
	private static String password;
	private static String dbName;
	private static boolean isSSLEnabled;
	private static boolean isSSLInvalidHostnameAllowed;

	public CustomMongoQuartzSchedulerJobStore() {
		super();
		initializeMongo();
		setMongoUri("mongodb://" + mongoAddresses);
		setUsername(userName);
		setPassword(password);
		setDbName(dbName);
		setMongoOptionEnableSSL(isSSLEnabled);
		setMongoOptionSslInvalidHostNameAllowed(isSSLInvalidHostnameAllowed);
	}

	/**
	 * <p>
	 * This method will initialize the mongo instance required by the Quartz
	 * scheduler.
	 * 
	 * The use case here is that we have two profiles;
	 * </p>
	 * 
	 * <ul>
	 * <li>Development</li>
	 * <li>Production</li>
	 * </ul>
	 * 
	 * <p>
	 * So when constructing the mongo instance to be used for the Quartz
	 * scheduler, we need to read the various properties set within the system
	 * to determine which would be appropriate depending on which spring profile
	 * is active.
	 * </p>
	 * 
	 */
	private static void initializeMongo() {
		/**
		 * The use case here is that when we run our application, the property
		 * spring.profiles.active is set as a system property during production.
		 * But it will not be set in a development environment.
		 */
		String env = System.getProperty(SystemProperties.ENVIRONMENT);
		env = StringUtils.isNotBlank(env) ? env : "dev";
		YamlPropertiesFactoryBean commonProperties = new YamlPropertiesFactoryBean();
		commonProperties.setResources(new ClassPathResource("application.yml"));
		/**
		 * The mongo DB user name and password are only password as command line
		 * parameters in the production environment and for the development
		 * environment it will be null which is why we use
		 * StringUtils#trimToEmpty so we can pass empty strings for the user
		 * name and password in the development environment since we do not have
		 * authentication on the development environment.s
		 */
		userName = StringUtils.trimToEmpty(commonProperties.getObject().getProperty(SystemProperties.SERVER_NAME));
		password = StringUtils.trimToEmpty(System.getProperty(SystemProperties.MONGO_PASSWORD));
		dbName = commonProperties.getObject().getProperty(SchedulerConstants.QUARTZ_SCHEDULER_DB_NAME);

		YamlPropertiesFactoryBean environmentSpecificProperties = new YamlPropertiesFactoryBean();

		userName = commonProperties.getObject().getProperty(SystemProperties.SERVER_NAME);

		switch (env) {
		case "prod":
			environmentSpecificProperties.setResources(new ClassPathResource("application-prod.yml"));
			/**
			 * By deafult, in the production mongo instance, SSL is enabled and
			 * SSL invalid host name allowed property is set.
			 */
			isSSLEnabled = true;
			isSSLInvalidHostnameAllowed = true;
			mongoAddresses = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_URI);
			break;
		case "dev":
			/**
			 * For the development profile, we just read the mongo URI that is
			 * set.
			 */
			environmentSpecificProperties.setResources(new ClassPathResource("application-dev.yml"));
			mongoAddresses = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_URI);
			break;

		}

	}

}
