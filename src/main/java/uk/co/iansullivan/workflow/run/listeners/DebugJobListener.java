package uk.co.iansullivan.workflow.run.listeners;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.iansullivan.workflow.jobs.Job;
import uk.co.iansullivan.workflow.jobs.JobContext;
import uk.co.iansullivan.workflow.jobs.JobResult;

public class DebugJobListener extends JobListenerBase {

    private static final DateFormat format = new SimpleDateFormat("H:m:s");
    
    @Override
    public void onJobFailure(Job job, JobResult result) {
        print(job, "failed");
    }
    
    @Override
    public void onJobPreExecute(Job job, JobContext context) {
        print(job, "pre-execute");
    }
    
    @Override
    public void onJobSuccess(Job job, JobResult result) {
        print(job, "success");
    }
    
    private static void print(Job job, String event) {
        String message = String.format("%s - Job '%s' %s", 
                format.format(new Date()),
                job.getClass().getSimpleName(),
                event);
        
        System.out.println(message);
    }
    
}
