package uk.co.iansullivan.workflow.containers;

import uk.co.iansullivan.workflow.containers.executors.JobContainerExecutor;

public interface JobContainer {
    JobContainerExecutor createExecutor();
}
