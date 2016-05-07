package uk.co.iansullivan.workflow.run;

import java.util.Map;

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
        return run(root, configuration, null);
    }
    
    public static JobResult run(JobContainer root, Map<String, Object> initalParameters) {
        return run(root, new WorkflowConfiguration(), initalParameters);
    }
    
    public static JobResult run(JobContainer root, WorkflowConfiguration configuration, Map<String, Object> initalParameters) {
       JobParameters initialJobParameters = new JobParameters(initalParameters);
       JobRunner jobRunner = configuration.getJobRunner();
       JobListener jobListener = configuration.getJobListener();
       WorkflowContext context = new WorkflowContext(jobRunner, jobListener);
       JobContainerExecutor rootExecutor = root.createExecutor();
       
       JobExecution rootResult = rootExecutor.execute(context, initialJobParameters);
         
       while(!rootResult.isFinished()) {
           jobRunner.blockUntilNextJobFinishes(jobListener);
           rootExecutor.step();
       }
       
       return rootResult.getJobResult();        
   }
}
