package uk.co.iansullivan.workflow.containers.executors;

import uk.co.iansullivan.workflow.jobs.JobParameters;
import uk.co.iansullivan.workflow.jobs.JobResult;
import uk.co.iansullivan.workflow.run.JobExecution;
import uk.co.iansullivan.workflow.run.WorkflowContext;

public class ExecutorController {
    
    private final JobContainerExecutor executor;
    private boolean isStarted;
    private JobExecution result;
    
    public ExecutorController(JobContainerExecutor executor) {
        this.executor = executor;
    }
    
    public void execute(WorkflowContext context, JobParameters inputParameters) {
        if (isStarted) {
            throw new UnsupportedOperationException("Executor already running");
        }
        
        result = executor.execute(context, inputParameters);
        isStarted = true;
    }
    
    public boolean isStarted() {
        return isStarted;
    }
    
    public boolean isRunning() {
        return isStarted() && !isFinished();
    }
    
    public boolean isFinished() {
        return result != null && result.isFinished();
    }
    
    public boolean isFailed() {
        return result != null && result.isFinished() && !result.getJobResult().isSuccess();
    }
    
    public boolean isSuccess() {
        return isFinished() && !isFailed();
    }
    
    public void stepIfRunning() {
        if (isRunning()) {
            executor.step();
        }
    }
    
    public JobResult getJobResult() {
        return result.getJobResult();
    }
    
}
