package uk.co.iansullivan.workflow.run.runners;

import uk.co.iansullivan.workflow.jobs.Job;
import uk.co.iansullivan.workflow.jobs.JobContext;
import uk.co.iansullivan.workflow.jobs.JobResult;
import uk.co.iansullivan.workflow.run.CompletedJob;
import uk.co.iansullivan.workflow.run.listeners.JobListener;

public class JobRunnerHelper {
    
    public static JobResult run(JobContext jobContext, Job job) {
        
        JobResult jobResult;
        
        try {
            jobResult = job.execute(jobContext);
        } catch(Exception e) {
            jobResult = JobResult.Failure("Job execution threw an exception", e);
        }
        
        return jobResult;
        
    }
    
    public static void onJobPreExecute(JobListener listener, Job job, JobContext context) {
        listener.onJobPreExecute(job, context);
    }
    
    public static void onJobComplete(JobListener listener, CompletedJob completedJob) {
        listener.onJobFinished(completedJob.getJob(), completedJob.getJobResult());
        if (completedJob.getJobResult().isSuccess()) {
            listener.onJobSuccess(completedJob.getJob(), completedJob.getJobResult());
        } else {
            listener.onJobFailure(completedJob.getJob(), completedJob.getJobResult());
        }
        completedJob.getJobExecution().complete(completedJob.getJobResult());
    }
}
