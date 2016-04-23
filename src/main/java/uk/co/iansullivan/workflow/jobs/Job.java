package uk.co.iansullivan.workflow.jobs;

public interface Job {

  JobResult execute(JobContext context);
  
}
