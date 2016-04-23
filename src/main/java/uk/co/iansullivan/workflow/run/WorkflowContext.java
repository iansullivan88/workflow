package uk.co.iansullivan.workflow.run;

import uk.co.iansullivan.workflow.jobs.Job;
import uk.co.iansullivan.workflow.jobs.JobContext;
import uk.co.iansullivan.workflow.run.listeners.JobListener;
import uk.co.iansullivan.workflow.run.runners.JobRunner;

public class WorkflowContext {
    
    private final JobRunner jobRunner;
    private final JobListener jobListener;
      
    public WorkflowContext(JobRunner jobRunner, JobListener jobListener) {
        this.jobRunner = jobRunner;
        this.jobListener = jobListener;
    }
    
    public JobExecution runJob(JobContext context, Job job) {
        return jobRunner.runJob(jobListener, context, job);
    }   
}
