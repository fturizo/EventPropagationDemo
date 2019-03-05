package fish.payara.demos.eventpropagation.common;

import java.io.Serializable;

/**
 * Used to represent data sent as payload of a clustered CDI event.
 * @author Fabio Turizo
 */
public class InstanceEventData implements Serializable{
    
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    
    private String instance;
    private ClusteredEventData data;

    public InstanceEventData() {
    }

    public InstanceEventData(String instance, ClusteredEventData data) {
        this.instance = instance;
        this.data = data;
    }

    public String getInstance() {
        return instance;
    }

    public ClusteredEventData getData() {
        return data;
    }
}
