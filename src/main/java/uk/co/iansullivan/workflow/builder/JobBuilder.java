package uk.co.iansullivan.workflow.builder;

import java.util.ArrayList;
import java.util.List;

import uk.co.iansullivan.workflow.containers.ForEachJobContainer;
import uk.co.iansullivan.workflow.containers.JobContainer;
import uk.co.iansullivan.workflow.containers.JobContainerDependency;
import uk.co.iansullivan.workflow.containers.MultipleJobContainer;
import uk.co.iansullivan.workflow.containers.SingleJobContainer;
import uk.co.iansullivan.workflow.jobs.Job;

public class JobBuilder {

    private final List<JobContainer> children = new ArrayList<>();
    private final List<JobContainerDependency> dependencies = new ArrayList<>();
    
    private JobBuilder() {
        
    }
    
    public static JobBuilder start() {
        return new JobBuilder();
    }
    
    public JobBuilder add(JobContainer container) {
        children.add(container);
        return this;
    }
    
    public JobBuilder add(Job job) {
        return add(new SingleJobContainer(job));
    }
       
    public JobBuilder then(JobContainer container) {
        if (!children.isEmpty()) {
            dependencies.add(new JobContainerDependency(children.size(), children.size() - 1));
        }
        
        return add(container);
    }
    
    public JobBuilder then(Job job) {
        return then(new SingleJobContainer(job));
    }
    
    public JobBuilder dependsOn(int dependantIndex) {
        if (dependantIndex == children.size() - 1) {
            throw new UnsupportedOperationException("Container can't depenend on itself");
        }
        if (dependantIndex >= children.size()) {
            throw new UnsupportedOperationException("No container at index " + dependantIndex);
        }
        dependencies.add(new JobContainerDependency(children.size() - 1, dependantIndex));
        return this;
    }
    
    public JobBuilder followedBy(int nextIndex) {
        if (nextIndex == children.size() - 1) {
            throw new UnsupportedOperationException("Container can't follow itself");
        }
        if (nextIndex >= children.size()) {
            throw new UnsupportedOperationException("No container at index " + nextIndex);
        }
        dependencies.add(new JobContainerDependency(nextIndex, children.size() - 1));
        return this;
    }
    
    public MultipleJobContainer create() {
        return new MultipleJobContainer(children, dependencies);
    }
    
    public ForEachJobContainer createForEach(String collectionParameterName, String loopParameterName) {
        return new ForEachJobContainer(children, dependencies, collectionParameterName, loopParameterName);
    }
    
    
}
