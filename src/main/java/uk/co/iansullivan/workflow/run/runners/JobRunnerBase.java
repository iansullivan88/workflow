package uk.co.iansullivan.workflow.run.runners;

import uk.co.iansullivan.workflow.run.listeners.JobListener;

public abstract class JobRunnerBase implements JobRunner {
    
    @Override
    public void shutdown() {}
    
    @Override
    public void blockUntilNextJobFinishes(JobListener jobListener) {}
}
