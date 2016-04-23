package uk.co.iansullivan.workflow.run.runners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolJobRunner extends ConcurrentJobRunner {

    public ThreadPoolJobRunner(int nThreads) {
        super(Executors.newFixedThreadPool(nThreads));
    }
    
    @Override
    public void shutdown() {
        ((ExecutorService) getExecutor()).shutdown();
    }

}
