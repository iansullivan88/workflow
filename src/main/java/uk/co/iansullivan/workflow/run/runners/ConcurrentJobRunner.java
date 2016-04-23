package uk.co.iansullivan.workflow.run.runners;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import uk.co.iansullivan.workflow.jobs.Job;
import uk.co.iansullivan.workflow.jobs.JobContext;
import uk.co.iansullivan.workflow.jobs.JobResult;
import uk.co.iansullivan.workflow.run.CompleteableJobExecution;
import uk.co.iansullivan.workflow.run.CompletedJob;
import uk.co.iansullivan.workflow.run.JobExecution;
import uk.co.iansullivan.workflow.run.listeners.JobListener;

public class ConcurrentJobRunner extends JobRunnerBase {

    private final ExecutorCompletionService<CompletedJob> completionService;
    private final Executor executor;
    
    public ConcurrentJobRunner(Executor executor) {
        this.executor = executor;
        completionService = new ExecutorCompletionService<>(executor);
    }

    @Override
    public JobExecution runJob(JobListener jobListener, final JobContext context, final Job job) {
        JobRunnerHelper.onJobPreExecute(jobListener, job, context);
        CompleteableJobExecution execution = new CompleteableJobExecution();
        completionService.submit(() -> {
            JobResult result = JobRunnerHelper.run(context, job);
            return new CompletedJob(job, result, execution);
        });
        return execution;
    }
    
    public void blockUntilNextJobFinishes(JobListener jobListener) {
        Future<CompletedJob> result = completionService.poll();
        if (result != null) {
            try {
                JobRunnerHelper.onJobComplete(jobListener, result.get());
            } catch (Exception e) {
                throw new UnsupportedOperationException("Interupting is not supported");
            }
        }
    }
    
    protected Executor getExecutor() {
        return executor;
    }
}
