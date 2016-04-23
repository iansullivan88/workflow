package uk.co.iansullivan.workflow.run;

import uk.co.iansullivan.workflow.run.listeners.JobListener;
import uk.co.iansullivan.workflow.run.listeners.JobListenerBase;
import uk.co.iansullivan.workflow.run.runners.JobRunner;
import uk.co.iansullivan.workflow.run.runners.SerialJobRunner;

public class WorkflowConfiguration {

    private JobRunner jobRunner = new SerialJobRunner();
   private JobListener jobListener = new JobListenerBase();
    
    public WorkflowConfiguration() {
        
    }
       
    public JobRunner getJobRunner() {
        return jobRunner;
    }
    
    public JobListener getJobListener() {
        return jobListener;
    }
    
    public WorkflowConfiguration setJobRunner(JobRunner jobRunner) {
        this.jobRunner = jobRunner;
        return this;
    }
    
    public WorkflowConfiguration setJobListener(JobListener jobListener) {
        this.jobListener = jobListener;
        return this;
    }
    
}
