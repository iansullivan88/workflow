package uk.co.iansullivan.workflow.containers.executors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import uk.co.iansullivan.workflow.containers.MultipleJobContainer;
import uk.co.iansullivan.workflow.jobs.JobParameters;
import uk.co.iansullivan.workflow.jobs.JobResult;
import uk.co.iansullivan.workflow.run.JobExecution;
import uk.co.iansullivan.workflow.run.WorkflowContext;

public class MultipleJobContainerExecutor extends JobContainerExecutorBase {

    private MultipleJobContainer container;
    private List<ExecutorController> executors;
    private WorkflowContext workflowContext;
    private JobParameters inputParameters;
    
    
    public MultipleJobContainerExecutor(MultipleJobContainer container) {
        this.container = container;
        executors = container.getChildren()
            .stream()
            .map(c -> new ExecutorController(c.createExecutor()))
            .collect(Collectors.toList());
    }

    @Override
    public JobExecution execute(WorkflowContext context, JobParameters inputParameters) {
        this.inputParameters = inputParameters;
        this.workflowContext = context;
        
        startReadyJobs();
        
        return getContainerResult();
    }

    @Override
    public void step() {
        if (getContainerResult().isFinished()) {
            return;
        }
        
        Optional<JobResult> failedChild = executors.stream()
            .filter(e -> e.isFailed())
            .map(e -> e.getJobResult())
            .findAny();
        
        if (failedChild.isPresent()) {
            getContainerResult().complete(failedChild.get());
            cleanUp();
        } else if (executors.stream().allMatch(e -> e.isSuccess())) {    
            getContainerResult().complete(JobResult.Success());
            cleanUp();
        }        
        else {        
            executors.stream().forEach(e -> e.stepIfRunning());
            startReadyJobs();
        }
        
        
    }
    
    private void startReadyJobs() {

        executors.stream()
            .filter(e -> !e.isStarted())
            .filter(e -> isJobExecutorReady(e))
            .forEach(e -> e.execute(workflowContext, getExecutionParameters(e)));
    }
    
    private boolean isJobExecutorReady(ExecutorController executor) {
        int index = executors.indexOf(executor);
        return container.getJobContainerDependencies()
            .stream()
            .filter(d -> d.getJobIndex() == index)
            .map(d -> d.getPreceedingJobIndex())
            .allMatch(i -> executors.get(i).isSuccess());
    }
    
    private JobParameters getExecutionParameters(ExecutorController executor) {
        JobParameters jobParameters = inputParameters;

        // It would be nice to use reduce but combining job parameters isn't associative
        List<ExecutorController> ancestors = getAncestors(executor)
                .stream()
                .filter(e -> e.isSuccess())
                .collect(Collectors.toList());
        
        Collections.reverse(ancestors);
        
        for(ExecutorController ancestor : ancestors) {
            jobParameters = new JobParameters(jobParameters, ancestor.getJobResult().getOutputParameters());
        }
        
        return jobParameters;
    }
     
    private List<ExecutorController> getAncestors(ExecutorController executor) {
        int index = executors.indexOf(executor);
        Set<Integer> visitedIndicies = new HashSet<Integer>();
        Queue<Integer> indiciesToProcess = new LinkedList<Integer>();
        List<ExecutorController> ancestors = new ArrayList<ExecutorController>();
        
        indiciesToProcess.add(index);
        visitedIndicies.add(index); 

        // Add all ancestors starting from closest ancestor
        
        while(!indiciesToProcess.isEmpty()) {
            Integer indexToProcess = indiciesToProcess.remove();
            
            List<Integer> parents = container.getJobContainerDependencies()
                .stream()
                .filter(d -> d.getJobIndex() == indexToProcess)
                .map(d -> d.getPreceedingJobIndex())
                .filter(i -> !visitedIndicies.contains(i))
                .sorted()
                .collect(Collectors.toList());
            
            ancestors.addAll(parents.stream()
                    .map(i -> executors.get(i))
                    .collect(Collectors.toList()));
            
            indiciesToProcess.addAll(parents); 
        }
        
        
        return ancestors;
    }
    
    // Child contains might contain might use a lot of memory so
    // after the container has executed, remove unneeded references so the
    // GC can do its thing.
    private void cleanUp() {
        container = null;
        executors = null;
        workflowContext = null;
        inputParameters = null;
    }
    
}
