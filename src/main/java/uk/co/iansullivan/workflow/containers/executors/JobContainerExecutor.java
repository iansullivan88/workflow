package uk.co.iansullivan.workflow.containers.executors;

import uk.co.iansullivan.workflow.jobs.JobParameters;
import uk.co.iansullivan.workflow.run.JobExecution;
import uk.co.iansullivan.workflow.run.WorkflowContext;

public interface JobContainerExecutor {
    JobExecution execute(WorkflowContext context, JobParameters inputParameters);
    void step();
}
