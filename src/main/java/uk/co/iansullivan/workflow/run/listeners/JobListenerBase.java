package uk.co.iansullivan.workflow.run.listeners;

import uk.co.iansullivan.workflow.jobs.Job;
import uk.co.iansullivan.workflow.jobs.JobContext;
import uk.co.iansullivan.workflow.jobs.JobResult;

public class JobListenerBase implements JobListener {

    @Override
    public void onJobPreExecute(Job job, JobContext context) {
    }

    @Override
    public void onJobFinished(Job job, JobResult result) {
    }

    @Override
    public void onJobSuccess(Job job, JobResult result) {
    }

    @Override
    public void onJobFailure(Job job, JobResult result) {
    }

}
