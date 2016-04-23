package uk.co.iansullivan.workflow.containers.executors;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import uk.co.iansullivan.workflow.containers.ForEachJobContainer;
import uk.co.iansullivan.workflow.jobs.JobParameters;
import uk.co.iansullivan.workflow.jobs.JobResult;
import uk.co.iansullivan.workflow.run.JobExecution;
import uk.co.iansullivan.workflow.run.WorkflowContext;

public class ForEachJobContainerExecutor extends JobContainerExecutorBase {

    private final ForEachJobContainer container;
    private Collection<ExecutorController> loopExecutors;
    
    public ForEachJobContainerExecutor(ForEachJobContainer container) {
        this.container = container;
    }
    
    
    @Override
    public JobExecution execute(WorkflowContext context, final JobParameters inputParameters) {
        if (!inputParameters.containsKey(container.getCollectionParameterName())) {
            String message = String.format(
                    "Input parameters do not contain collection variable '%s'",
                    container.getCollectionParameterName());
            
            getContainerResult().complete(JobResult.Failure(message));
        } else {
            Object collection = inputParameters.getValue(container.getCollectionParameterName());
            if (collection == null) {
                String message = String.format(
                    "Collection parameter '%s' is null",
                    container.getCollectionParameterName());
                
                getContainerResult().complete(JobResult.Failure(message));
            } else if (!Iterable.class.isAssignableFrom(collection.getClass())) {
                String message = String.format(
                    "Collection parameter '%s' is not iterable",
                    container.getCollectionParameterName());
                    
                    getContainerResult().complete(JobResult.Failure(message));
            } else {
                Iterable<?> iterable = (Iterable<?>)collection;
                loopExecutors = StreamSupport.stream(iterable.spliterator(), false)
                    .map((Object l) -> {
                        JobParameters loopParameters = new JobParameters(
                                inputParameters,
                                container.getLoopParameterName(),
                                l);
                        
                        ExecutorController executor = new ExecutorController(new MultipleJobContainerExecutor(container));
                        executor.execute(context, loopParameters);
                        return executor;
                    }).collect(Collectors.toList());
                    
            }
        }
        
        return getContainerResult();
    }

    @Override
    public void step() {
        if (getContainerResult().isFinished()) {
            return;
        }
        
        if (loopExecutors.stream().allMatch(e -> e.isFinished())) {
            if (loopExecutors.stream().allMatch(e -> e.isSuccess())) {
                getContainerResult().complete(JobResult.Success());
            } else {
                getContainerResult().complete(loopExecutors.stream()
                        .filter(e -> e.isFailed())
                        .findFirst().get().getJobResult());
            }
        }

        loopExecutors.stream().forEach(e -> e.stepIfRunning());   
    }

}
