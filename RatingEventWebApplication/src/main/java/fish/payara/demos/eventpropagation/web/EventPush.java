package fish.payara.demos.eventpropagation.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Fabio Turizo
 */
@ServerEndpoint("/data")
public class EventPush {

    private static final Logger LOG = Logger.getLogger(EventPush.class.getName());
    
    private Session currentSession;
    
    @Inject
    RatingSessionManager sessionManager;
    
    @OnOpen
    public void onOpen(Session session) {
        LOG.log(Level.INFO, "Opening session {0}", session.getId());
        this.currentSession = session;
        sessionManager.registerSession(session);
    }
    
    @OnClose
    public void onClose(Session session) {
        LOG.log(Level.INFO, "Closing session {0}", session.getId());
        sessionManager.deregisterSession(session);
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        return null;
    }

    @OnError
    public void onError(Throwable error) {
        sessionManager.deregisterSession(currentSession);
        LOG.log(Level.SEVERE, "Error occurred session " + currentSession.getId(), error);
    }
}
