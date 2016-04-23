package uk.co.iansullivan.workflow.run;

import uk.co.iansullivan.workflow.jobs.JobResult;

public class CompleteableJobExecution implements JobExecution {

    private JobResult result;
    
    @Override
    public boolean isFinished() {
        return result != null;
    }

    @Override
    public JobResult getJobResult() {
        if (result == null) {
            throw new UnsupportedOperationException("Cannot get a job result until the job is finished");
        }
        return result;
    }
    
    public void complete(JobResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Cannot set a null result");
        }
        
        this.result = result;
    }

}
