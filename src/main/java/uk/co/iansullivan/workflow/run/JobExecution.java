package uk.co.iansullivan.workflow.run;

import uk.co.iansullivan.workflow.jobs.JobResult;

public interface JobExecution {
    boolean isFinished();
    JobResult getJobResult(); 
}
