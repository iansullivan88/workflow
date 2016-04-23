package uk.co.iansullivan.workflow.run.runners;

import uk.co.iansullivan.workflow.jobs.Job;
import uk.co.iansullivan.workflow.jobs.JobContext;
import uk.co.iansullivan.workflow.run.JobExecution;
import uk.co.iansullivan.workflow.run.listeners.JobListener;

public interface JobRunner {

    JobExecution runJob(JobListener jobListener, JobContext context, Job job);
    void blockUntilNextJobFinishes(JobListener jobListener);
    void shutdown();
}