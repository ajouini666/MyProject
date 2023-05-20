package com.example.packageassignment.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledFuture;

import com.example.packageassignment.controller.PackageController;
import com.example.packageassignment.service.SchedulingPropertiesService;

@Configuration
public class SchedulingConfiguration implements SchedulingConfigurer {

    @Autowired
    private PackageController packageController;

    public SchedulingConfiguration(PackageController packageController) {
        this.packageController = packageController;
    }

    @Autowired
    private SchedulingPropertiesService schedulingPropertiesService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.initialize();

        // Set the taskScheduler in the PackageController
        packageController.setTaskScheduler(scheduler);

        // Create the task to be scheduled
        Runnable task = packageController.getRunnable();
        packageController.setTask(task);

        // Check the scheduling status before starting the task
        if (schedulingPropertiesService.getScheduleEnabled()) {
            CronTrigger cronTrigger = new CronTrigger(packageController.getTrigger().getExpression());

            // Schedule the initial task
            ScheduledFuture<?> scheduledFuture = scheduler.schedule(task, cronTrigger);
            packageController.setScheduledFuture(scheduledFuture);
        }

        // Set the taskRegistrar in the PackageController
        packageController.setTaskRegistrar(taskRegistrar);
    }

}