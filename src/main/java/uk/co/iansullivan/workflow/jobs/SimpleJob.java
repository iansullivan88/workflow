package uk.co.iansullivan.workflow.jobs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SimpleJob extends BaseJob {

    private final Method runMethod;
    
    public SimpleJob() {
        Optional<Method> run = Arrays.stream(getClass().getDeclaredMethods())
            .filter(m -> m.getName().equals("run"))
            .findFirst();
        
        if (!run.isPresent()) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not have a 'run' method");
        }
        
        runMethod = run.get();

    }
    
    @Override
    public JobResult execute(JobContext context) {
        List<Object> parameterValues = new ArrayList<>();
        
        for(Parameter parameter : runMethod.getParameters()) {
            Param paramAnnotation = parameter.getAnnotation(Param.class);
            
            if (paramAnnotation == null) {
                return JobResult.Failure(this.getClass().getSimpleName() + ": run method parameter is missing 'Param' annotation");
            }
            
            String parameterName = paramAnnotation.value();
            
            if (!context.getInputParameters().containsKey(parameterName)) {
                return JobResult.Failure(String.format("%s: Missing input parameter '%s'", this.getClass().getSimpleName(), parameterName));
            }
            
            Class<?> parameterType = parameter.getType();
            Object value = context.getInputParameters().getValue(parameterName);
            
            if (value == null) {
                return JobResult.Failure(String.format("%s: Input parameter '%s' is null", this.getClass().getSimpleName(), parameterName));
            }
            
            if (value != null && !parameterType.isAssignableFrom(value.getClass())) {
                return JobResult.Failure(String.format("%s: Input parameter '%s' cannot accept value of type '%s'",
                        this.getClass().getSimpleName(), parameterName, value.getClass().getSimpleName()));
            }
            
            parameterValues.add(value);            
        }
        
        Object result;
        try {
            result = runMethod.invoke(this, parameterValues.toArray());            
        } catch(InvocationTargetException e) {
            if (e instanceof Exception) {
                return JobResult.Failure((Exception)e.getTargetException());
            }
            throw new Error(e.getTargetException());
            
        } catch (Exception e) {
            return JobResult.Failure(e);
        }
        
        if (result instanceof JobResult) {
            return (JobResult)result;
        }
        
        OutputParam outputAnnotation = runMethod.getAnnotation(OutputParam.class);
        
        if (outputAnnotation != null && !outputAnnotation.value().isEmpty() && result != null) {
            return JobResult.Success(outputAnnotation.value(), result);
        }
        
        return JobResult.Success();
        
    }    
    
}
