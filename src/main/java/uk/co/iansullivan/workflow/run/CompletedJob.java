package uk.co.iansullivan.workflow.run;

import uk.co.iansullivan.workflow.jobs.Job;
import uk.co.iansullivan.workflow.jobs.JobResult;

public class CompletedJob {
    private final Job job;
    private final JobResult jobResult;
    private final CompleteableJobExecution jobExecution;
    
    public CompletedJob(Job job, JobResult jobResult, CompleteableJobExecution jobExecution) {
        this.job = job;
        this.jobResult = jobResult;
        this.jobExecution = jobExecution;
    }
    
    public CompleteableJobExecution getJobExecution() {
        return jobExecution;
    }
    
    public Job getJob() {
        return job;
    }
    
    public JobResult getJobResult() {
        return jobResult;
    }
}
