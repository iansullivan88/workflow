package uk.co.iansullivan.workflow.run.listeners;

import uk.co.iansullivan.workflow.jobs.Job;
import uk.co.iansullivan.workflow.jobs.JobContext;
import uk.co.iansullivan.workflow.jobs.JobResult;

public interface JobListener {
    void onJobPreExecute(Job job, JobContext context);
    void onJobFinished(Job job, JobResult result);
    void onJobSuccess(Job job, JobResult result);
    void onJobFailure(Job job, JobResult result);
}
