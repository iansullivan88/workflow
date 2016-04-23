package uk.co.iansullivan.workflow.containers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.co.iansullivan.workflow.containers.executors.JobContainerExecutor;
import uk.co.iansullivan.workflow.containers.executors.MultipleJobContainerExecutor;

public class MultipleJobContainer extends JobContainerBase {

    private List<JobContainer> children;
    private Collection<JobContainerDependency> jobContainerDependencies;
    
    public MultipleJobContainer(List<JobContainer> children) {
        this(children, new ArrayList<JobContainerDependency>());
    }
    
    public MultipleJobContainer(List<JobContainer> children, Collection<JobContainerDependency> jobContainerDependencies) {
        this.children = children;
        this.jobContainerDependencies = jobContainerDependencies;
    }

    @Override
    public JobContainerExecutor createExecutor() {
        return new MultipleJobContainerExecutor(this);
    }
    
    public List<JobContainer> getChildren() {
        return children;
    }
    
    public Collection<JobContainerDependency> getJobContainerDependencies() {
        return jobContainerDependencies;
    }
   
    
}
