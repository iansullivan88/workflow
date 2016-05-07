# Workflow
A Java library for organising code as a series of jobs.

# Example
Imagine an online shop that has a process that runs every hour that looks like:

![process](/process-diagram.png)

Each box represents a 'job'. Arrows show the order of execution.

## Jobs

To create a job in code, the `Job` interface must be implemented:

    public interface Job {
        JobResult execute(JobContext context);
    }
    
It is however, much easier to subclass `SimpleJob` as it automatically resolves parameters using annotations. A subclass of `SimpleJob` expects there to be a run method. For example, this is how the 'Notify Process Start' job is defined:

    public class NotifyProcessStartJob extends SimpleJob {

        public void run(@Param("emailClient") EmailClient emailClient){
            emailClient.sendEmail("admin@warehouse.com", "Worflow has started");
        }
        
    }
    
An instance of `EmailClient` is passed in. The `@Param` annotation says 'inject the parameter called emailClient' - more on that later! Jobs that return nothing aren't always useful. It is possible to define a job that returns a value for example, the 'Get Orders' job:

    public static class GetOrdersJob extends SimpleJob {
        
        @OutputParam("orders")
        public List<Order> run(@Param("database") DatabaseContext database){
            return database.getReadyOrders();       
        }
        
    }
    
`GetOrdersJob` returns a parameter called `orders` which has the type `List<Order>`. So, how to parameters get passed from job to job? The simple rule is 'a job will have access to parameters that are output from ancestors and from all preceeding, connected jobs'. For example, the 'Notify Warehouse' job will have access to parameters output from 'Validate Order', 'Get Orders' and 'Notify Process Start'. It will also have access to parameters that are passed in to the root container (that contains all jobs).

One final interesting job is a job that can fail:

    public static class ValidateOrderJob extends SimpleJob {
        
        public JobResult run(@Param("order")Order order){
            if (order.getNumberOfItems() > 0 && order.getHasBillBeenSettled()) {
                return JobResult.Success();
            } else {
                return JobResult.Failure("Order " + order.getId() + " failed validation");
            }
        }
        
    }
    
`run` can return an object of type `JobResult` that can represent a success or failure and can also be used to return multiple parameters. Error handling at the moment isn't very smart - if a job fails, the error is propagated up to the root and the entire job fails with that error, a bit like an exception.

That's all there is to creating jobs - simple methods!

## Job Containers

There are three classes that implement the `JobContainer` interface. `SingleJobContainer` is a container that contains a single job. To execute this container, its job is executed. `MultipleJobContainer` is a container that has multiple child job containers. A `ForEachJobContainer` is a subclass of `MultipleJobContainer` that executes itself once for every item in a `Iterable<?>`. That's a confusing description, and isn't really important. There is a nice fluent api for creating containers. The workflow in the diagram above can be created as follows:

    JobContainer workflow = JobBuilder.start()
        .add(new NotifyProcessStartJob())
        .then(new GetOrdersJob())
        .then(JobBuilder.start()
            .add(new ValidateOrderJob())
            .add(new NotifyWarehouseJob()).dependsOn(0)
            .add(new EmailReceiptJob()).dependsOn(0)
            .add(new MarkReadyForDispatchJob()).dependsOn(0)
            .createForEach("orders", "order"))
        .then(new NotifyProcessEndJob())
        .add(new GetItemsLowOnStockJob()).dependsOn(0)
        .then(new RequestNewStockJob()).followedBy(3)
        .create();

`add` adds a new child container, `then` adds a new child container that runs after the previous added child, `dependsOn` ensures a container runs after the container at the specified index and `followedBy` specifies that the container at the specified runs after the most recently added container. `create` creates a `MultipleJobContainer` from the specified jobs and ordering. `createForEach` creates a `ForEachJobContainer` by specifing the name of the parameter is iterated over and the paramter that will be available on each iteration. In the example above, there will be an execution of the ForEach container for each order and each order will be bound to the parameter called `order`.

## Running Containers

It is very easy to run the workflow above like so:

    JobResult result = Workflow.run(workflow);

`result.isSuccess()` is false in this case. The error message is "NotifyProcessStartJob: Missing input parameter 'emailClient'". That job was defined above and its `run` method required a parameter called `emailClient` of type `EmailClient`. Other jobs also require a `DabaseConxtext` called `database`. It's easy to make these parameters available to all jobs by passing them to the `run` method:

    Map<String, Object> rootParameters = new HashMap<>();
    rootParameters.put("database", new DatabaseContext());
    rootParameters.put("emailClient", new SmtpClient());
    
    JobResult result = Workflow.run(workflow, rootParameters);

`result.isSuccess()` is true now! It's easy to verify the jobs are running by using a class that implements `JobListener`. The interface looks like:

    public interface JobListener {
        void onJobPreExecute(Job job, JobContext context);
        void onJobFinished(Job job, JobResult result);
        void onJobSuccess(Job job, JobResult result);
        void onJobFailure(Job job, JobResult result);
    }

These methods fire for every executed job. To verify the jobs run, there is an implementation of `JobListener` that prints to `System.out`. It can be used as followed:

    WorkflowConfiguration configuration = new WorkflowConfiguration()
        .setJobListener(new DebugJobListener());

    JobResult result = Workflow.run(workflow, configuration, rootParameters);
    
This gives the following output:

    20:17:30 - Job 'NotifyProcessStartJob' pre-execute
    20:17:32 - Job 'NotifyProcessStartJob' success
    20:17:32 - Job 'GetOrdersJob' pre-execute
    20:17:33 - Job 'GetOrdersJob' success
    20:17:33 - Job 'ValidateOrderJob' pre-execute
    20:17:35 - Job 'ValidateOrderJob' success
    20:17:35 - Job 'NotifyWarehouseJob' pre-execute
    20:17:36 - Job 'NotifyWarehouseJob' success
    20:17:36 - Job 'EmailReceiptJob' pre-execute
    20:17:38 - Job 'EmailReceiptJob' success
    20:17:38 - Job 'MarkReadyForDispatchJob' pre-execute
    20:17:39 - Job 'MarkReadyForDispatchJob' success
    20:17:39 - Job 'ValidateOrderJob' pre-execute
    20:17:41 - Job 'ValidateOrderJob' success
    20:17:41 - Job 'NotifyWarehouseJob' pre-execute
    20:17:42 - Job 'NotifyWarehouseJob' success
    20:17:42 - Job 'EmailReceiptJob' pre-execute
    20:17:44 - Job 'EmailReceiptJob' success
    20:17:44 - Job 'MarkReadyForDispatchJob' pre-execute
    20:17:45 - Job 'MarkReadyForDispatchJob' success
    20:17:45 - Job 'GetItemsLowOnStockJob' pre-execute
    20:17:47 - Job 'GetItemsLowOnStockJob' success
    20:17:47 - Job 'RequestNewStockJob' pre-execute
    20:17:48 - Job 'RequestNewStockJob' success
    20:17:48 - Job 'NotifyProcessEndJob' pre-execute
    20:17:50 - Job 'NotifyProcessEndJob' success

Each job takes a second and half to run. So it takes about 20 seconds to run the entire process. That's pretty bad! It's obvious from the diagram that there are two main paths of execution, these could easily be executed concurrently. Not only that, each iteration of the foreach container can be run concurrently. Instances of `JobRunner` are responsible for running jobs. The default implementation is `SerialJobRunner` which, as you might imagine, executes jobs one at a time. To execute jobs using a threadpool, use a `ThreadPoolJobRunner`:

    WorkflowConfiguration configuration = new WorkflowConfiguration()
        .setJobRunner(new ThreadPoolJobRunner(16))
        .setJobListener(new DebugJobListener());
        
    JobResult result = Workflow.run(workflow, configuration, rootParameters);
    
    // Shut down the thread pool 
    configuration.getJobRunner().shutdown();

Now you can see jobs that can execute in parallel, will:

    20:26:49 - Job 'NotifyProcessStartJob' pre-execute
    20:26:51 - Job 'NotifyProcessStartJob' success
    20:26:51 - Job 'GetOrdersJob' pre-execute
    20:26:51 - Job 'GetItemsLowOnStockJob' pre-execute
    20:26:52 - Job 'GetItemsLowOnStockJob' success
    20:26:52 - Job 'RequestNewStockJob' pre-execute
    20:26:52 - Job 'GetOrdersJob' success
    20:26:52 - Job 'ValidateOrderJob' pre-execute
    20:26:52 - Job 'ValidateOrderJob' pre-execute
    20:26:54 - Job 'RequestNewStockJob' success
    20:26:54 - Job 'ValidateOrderJob' success
    20:26:54 - Job 'NotifyWarehouseJob' pre-execute
    20:26:54 - Job 'EmailReceiptJob' pre-execute
    20:26:54 - Job 'MarkReadyForDispatchJob' pre-execute
    20:26:54 - Job 'ValidateOrderJob' success
    20:26:54 - Job 'NotifyWarehouseJob' pre-execute
    20:26:54 - Job 'EmailReceiptJob' pre-execute
    20:26:54 - Job 'MarkReadyForDispatchJob' pre-execute
    20:26:56 - Job 'NotifyWarehouseJob' success
    20:26:56 - Job 'MarkReadyForDispatchJob' success
    20:26:56 - Job 'EmailReceiptJob' success
    20:26:56 - Job 'NotifyWarehouseJob' success
    20:26:56 - Job 'EmailReceiptJob' success
    20:26:56 - Job 'MarkReadyForDispatchJob' success
    20:26:56 - Job 'NotifyProcessEndJob' pre-execute
    20:26:57 - Job 'NotifyProcessEndJob' success

# Why?

When using this library you sacrifice type safety but you get a few advantages to make up for it:
* The structure of the workflow is separted from the logic of the individual jobs. This keeps code modular and each job testable.
* Jobs can take interfaces as parameters so dependency injection is simple.
* Listeners allow you to write consistent logging for an entire workflow in a single place.
* It's easy to run jobs concurrently across multiple threads.

This library has several advtanges over various ETL libaries:
* The structure of the workflow is written in code. There are no large config files or GUIs required.
* It is extensible. Most of the classes work with interfaces so you can create your own implementations of containers, jobs, job runners and listeners.
* Jobs are written in pure code so can do anything Java can.

# Contributing
Please do!
