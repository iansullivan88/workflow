package uk.co.iansullivan.workflow.containers;

public class JobContainerDependency {

    private final int jobIndex;
    private final int preceedingJobIndex;
    
    public JobContainerDependency(int jobIndex, int preceedingJobIndex) {
        this.jobIndex = jobIndex;
        this.preceedingJobIndex = preceedingJobIndex;
    }
    
    public int getJobIndex() {
        return jobIndex;
    }
    
    public int getPreceedingJobIndex() {
        return preceedingJobIndex;
    }
    
    
}
