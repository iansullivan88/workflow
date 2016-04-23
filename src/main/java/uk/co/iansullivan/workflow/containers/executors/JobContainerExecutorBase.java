package uk.co.iansullivan.workflow.containers.executors;

import uk.co.iansullivan.workflow.run.CompleteableJobExecution;

public abstract class JobContainerExecutorBase implements JobContainerExecutor {

    private final CompleteableJobExecution containerResult = new CompleteableJobExecution();
    
    protected CompleteableJobExecution getContainerResult() {
        return containerResult;
    }
    
}
