package uk.co.iansullivan.workflow.jobs;

public class JobContext {

    private final JobParameters inputParameters;

    public JobContext(JobParameters inputParameters) {
        this.inputParameters = inputParameters;
    }

    public JobParameters getInputParameters() {
        return inputParameters;
    }

}