package uk.co.iansullivan.workflow.run.runners;

import uk.co.iansullivan.workflow.jobs.Job;
import uk.co.iansullivan.workflow.jobs.JobContext;
import uk.co.iansullivan.workflow.run.CompleteableJobExecution;
import uk.co.iansullivan.workflow.run.CompletedJob;
import uk.co.iansullivan.workflow.run.JobExecution;
import uk.co.iansullivan.workflow.run.listeners.JobListener;

public class SerialJobRunner extends JobRunnerBase {
   
    @Override
    public JobExecution runJob(JobListener jobListener, JobContext context, Job job) {
        JobRunnerHelper.onJobPreExecute(jobListener, job, context);
        CompleteableJobExecution jobExecution = new CompleteableJobExecution();
        CompletedJob result = new CompletedJob(job, JobRunnerHelper.run(context, job), jobExecution);
        JobRunnerHelper.onJobComplete(jobListener, result);
        return jobExecution;
    }
}
