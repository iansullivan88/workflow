package uk.co.iansullivan.workflow.containers;

import uk.co.iansullivan.workflow.containers.executors.JobContainerExecutor;
import uk.co.iansullivan.workflow.containers.executors.SingleJobContainerExecutor;
import uk.co.iansullivan.workflow.jobs.Job;

public class SingleJobContainer extends JobContainerBase {

    private final Job job;
    
    public SingleJobContainer(Job job) {
        this.job = job;
    }

    @Override
    public JobContainerExecutor createExecutor() {
        return new SingleJobContainerExecutor(this);
    }
    
    public Job getJob() {
        return job;
    }
    
}
