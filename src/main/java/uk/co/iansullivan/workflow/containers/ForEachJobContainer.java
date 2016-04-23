package uk.co.iansullivan.workflow.containers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.co.iansullivan.workflow.containers.executors.ForEachJobContainerExecutor;
import uk.co.iansullivan.workflow.containers.executors.JobContainerExecutor;

public class ForEachJobContainer extends MultipleJobContainer {

    private final String collectionParameterName;
    private final String loopParameterName;
    
    public ForEachJobContainer(
            List<JobContainer> children,
            Collection<JobContainerDependency> jobContainerDependencies,
            String collectionParameterName,
            String loopParameterName) {
        
        super(children, jobContainerDependencies);
        
        this.collectionParameterName = collectionParameterName;
        this.loopParameterName = loopParameterName;
    }
    
    public ForEachJobContainer(
            List<JobContainer> children,
            String collectionParameterName,
            String loopParameterName) {
        
        this(children, new ArrayList<JobContainerDependency>(), collectionParameterName, loopParameterName);
    }
    
    @Override
    public JobContainerExecutor createExecutor() {
        return new ForEachJobContainerExecutor(this);
    }
    
    public String getCollectionParameterName() {
        return collectionParameterName;
    }
    
    public String getLoopParameterName() {
        return loopParameterName;
    }

}
