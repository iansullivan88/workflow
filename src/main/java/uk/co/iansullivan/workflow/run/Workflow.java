package uk.co.iansullivan.workflow.run;

import uk.co.iansullivan.workflow.containers.JobContainer;
import uk.co.iansullivan.workflow.containers.executors.JobContainerExecutor;
import uk.co.iansullivan.workflow.jobs.JobParameters;
import uk.co.iansullivan.workflow.jobs.JobResult;
import uk.co.iansullivan.workflow.run.listeners.JobListener;
import uk.co.iansullivan.workflow.run.runners.JobRunner;

public class Workflow {
   
    public static JobResult run(JobContainer root) {
        return run(root, new WorkflowConfiguration());
    }
    
    public static JobResult run(JobContainer root, WorkflowConfiguration configuration) {
        return run(root, configuration, new JobParameters());
    }
    
    public static JobResult run(JobContainer root, WorkflowConfiguration configuration, JobParameters initalParameters) {
       JobRunner jobRunner = configuration.getJobRunner();
       JobListener jobListener = configuration.getJobListener();
       WorkflowContext context = new WorkflowContext(jobRunner, jobListener);
       JobContainerExecutor rootExecutor = root.createExecutor();
       
       JobExecution rootResult = rootExecutor.execute(context, initalParameters);
         
       while(!rootResult.isFinished()) {
           jobRunner.blockUntilNextJobFinishes(jobListener);
           rootExecutor.step();
       }
       
       return rootResult.getJobResult();        
   }
}
