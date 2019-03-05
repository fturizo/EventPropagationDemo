package fish.payara.demos.eventpropagation.web;

import fish.payara.demos.eventpropagation.common.InstanceEventData;
import fish.payara.demos.eventpropagation.common.RatingData;
import fish.payara.micro.cdi.Inbound;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import javax.websocket.EncodeException;
import javax.websocket.Session;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationScoped
public class RatingSessionManager {

    private static final Logger LOG = Logger.getLogger(RatingSessionManager.class.getName());
    private final Map<String, Session> sessionMap = new HashMap<>();

    @PostConstruct
    public void initialize() {
        LOG.info("Initializing rating session manager and observer");
    }

    public void registerSession(Session session) {
        sessionMap.put(session.getId(), session);
    }

    public void deregisterSession(Session session) {
        sessionMap.remove(session.getId());
    }

    public void observeClusteredData(@Observes @Inbound InstanceEventData eventData) {        
        if (eventData.getData() instanceof RatingData) {
            LOG.log(Level.FINE, "Received event {0} from instance " + eventData.getInstance(), eventData.getData());
            RatingData ratingData = (RatingData) eventData.getData();
            sessionMap.forEach((id, session) -> {
                try{
                    JsonObject data = Json.createObjectBuilder()
                                            .add("score", ratingData.getScore())
                                            .add("time", ratingData.getTime().toString())
                                            .add("message", ratingData.getMessage())
                                            .add("instance", eventData.getInstance())
                                            .build();
                    session.getBasicRemote().sendObject(data.toString());
                }catch(IOException | EncodeException exception){
                    LOG.log(Level.SEVERE, "Exception sending data to session", exception);
                }
            });
        }
    }
}
