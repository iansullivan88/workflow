package uk.co.iansullivan.workflow.jobs;

import java.util.HashMap;
import java.util.Map;

public class JobResult {

    private final boolean isSuccess;
    private final JobParameters outputParameters;
    private final String errorMessage;
    private final Exception exception;

  	private JobResult(boolean isSuccess, JobParameters outputParameters, String errorMessage, Exception exception) {
  	    this.isSuccess = isSuccess;
  	    this.outputParameters = outputParameters;
  	    this.errorMessage = errorMessage;
  	    this.exception = exception;
  	}

  	public static JobResult Failure(String errorMessage) {
  		return Failure(errorMessage, null);
  	}
  	
    public static JobResult Failure(String errorMessage, Exception exception) {
        return new JobResult(false, new JobParameters(), errorMessage, exception);
    }
    
    public static JobResult Failure(Exception exception) {
        return new JobResult(false, new JobParameters(), exception.getMessage(), exception);
    }
  	
  	public static JobResult Success(Map<String, Object> outputParameters) {
  		return new JobResult(true, new JobParameters(outputParameters), null, null);
  	}
  	
    public static JobResult Success(String parameterName, Object value) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameterName, value);
        return Success(parameters);
    }
  	
    public static JobResult Success() {
        return Success(null);
    }
  	
  	public boolean isSuccess() {
  		return isSuccess;
  	}
  	
  	public String getErrorMessage() {
  	    return errorMessage;
  	}
  	
  	public JobParameters getOutputParameters() {
  		return outputParameters;
  	}
  	
  	public Exception getException() {
  	    return exception;
  	}
  	
  	
}
