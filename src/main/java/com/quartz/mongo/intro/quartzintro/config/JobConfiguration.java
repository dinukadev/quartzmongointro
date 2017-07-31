package com.quartz.mongo.intro.quartzintro.config;

import static org.quartz.TriggerBuilder.newTrigger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.quartz.mongo.intro.quartzintro.constants.SchedulerConstants;
import com.quartz.mongo.intro.quartzintro.scheduler.jobs.SampleJob;

/**
 * 
 * This will configure the job to run within quartz.
 * 
 * @author dinuka
 *
 */
@Configuration
public class JobConfiguration {

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@PostConstruct
	private void initialize() throws Exception {
		schedulerFactoryBean.getScheduler().addJob(sampleJobDetail(), true, true);
		if (!schedulerFactoryBean.getScheduler().checkExists(new TriggerKey(
				SchedulerConstants.SAMPLE_JOB_POLLING_TRIGGER_KEY, SchedulerConstants.SAMPLE_JOB_POLLING_GROUP))) {
			schedulerFactoryBean.getScheduler().scheduleJob(sampleJobTrigger());
		}

	}

	/**
	 * <p>
	 * The job is configured here where we provide the job class to be run on
	 * each invocation. We give the job a name and a value so that we can
	 * provide the trigger to it on our method {@link #sampleJobTrigger()}
	 * </p>
	 * 
	 * @return an instance of {@link JobDetail}
	 */
	private static JobDetail sampleJobDetail() {
		JobDetailImpl jobDetail = new JobDetailImpl();
		jobDetail.setKey(
				new JobKey(SchedulerConstants.SAMPLE_JOB_POLLING_JOB_KEY, SchedulerConstants.SAMPLE_JOB_POLLING_GROUP));
		jobDetail.setJobClass(SampleJob.class);
		jobDetail.setDurability(true);
		return jobDetail;
	}

	/**
	 * <p>
	 * This method will define the frequency with which we will be running the
	 * scheduled job which in this instance is every minute three seconds after
	 * the start up.
	 * </p>
	 * 
	 * @return an instance of {@link Trigger}
	 */
	private static Trigger sampleJobTrigger() {
		return newTrigger().forJob(sampleJobDetail())
				.withIdentity(SchedulerConstants.SAMPLE_JOB_POLLING_TRIGGER_KEY,
						SchedulerConstants.SAMPLE_JOB_POLLING_GROUP)
				.withPriority(50).withSchedule(SimpleScheduleBuilder.repeatMinutelyForever())
				.startAt(Date.from(LocalDateTime.now().plusSeconds(3).atZone(ZoneId.systemDefault()).toInstant()))
				.build();
	}

}