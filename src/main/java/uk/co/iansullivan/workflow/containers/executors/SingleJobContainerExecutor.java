package uk.co.iansullivan.workflow.containers.executors;

import uk.co.iansullivan.workflow.containers.SingleJobContainer;
import uk.co.iansullivan.workflow.jobs.JobContext;
import uk.co.iansullivan.workflow.jobs.JobParameters;
import uk.co.iansullivan.workflow.run.JobExecution;
import uk.co.iansullivan.workflow.run.WorkflowContext;

public class SingleJobContainerExecutor extends JobContainerExecutorBase {

    private final SingleJobContainer container;
    private JobExecution jobResult;
    
    public SingleJobContainerExecutor(SingleJobContainer container) {
        this.container = container;
    }
    
    @Override
    public JobExecution execute(WorkflowContext context, JobParameters inputParameters) {      
        jobResult = context.runJob(new JobContext(inputParameters), container.getJob());
        step();
        return getContainerResult();
    }

    @Override
    public void step() {
        if (!getContainerResult().isFinished() && jobResult.isFinished()) {
            getContainerResult().complete(jobResult.getJobResult());
        }
    }

}