package uk.co.iansullivan.workflow.jobs;

import java.util.HashMap;
import java.util.Map;

public class JobParameters {
    
    private Map<String, Object> parameters;
     
    public JobParameters() {
        this.parameters = new HashMap<>();
    }
    
    public JobParameters(String key, Object value) {
        this();
        parameters.put(key,  value);
    }
    
    public JobParameters(Map<String, Object> parameters) {
        this.parameters = parameters == null ? new HashMap<String, Object>() : parameters;
    }
    
    public JobParameters(JobParameters originalParameters, JobParameters additionalParameters) {
        this(new HashMap<>(originalParameters.parameters));
        for(Map.Entry<String, Object> entry : additionalParameters.parameters.entrySet()) {
            parameters.put(entry.getKey(), entry.getValue());
        }
    }
    
    public JobParameters(JobParameters originalParameters, String key, Object value) {
        this(new HashMap<String, Object>(originalParameters.parameters));
        parameters.put(key, value);
    }
    
    public Object getValue(String key) {
        return parameters.get(key);
    }
    
    public boolean containsKey(String key) {
        return parameters.containsKey(key);
    }
    
    
    
}
