package fish.payara.demos.eventpropagator.generator.services;

import fish.payara.demos.eventpropagation.common.InstanceEventData;
import fish.payara.demos.eventpropagation.common.RatingData;
import fish.payara.demos.eventpropagator.generator.enumerations.Reaction;
import fish.payara.micro.PayaraMicro;
import fish.payara.micro.cdi.Outbound;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 *
 * @author Fabio Turizo
 */
@Singleton
public class RatingEventsGenerator {

    private static final Logger LOG = Logger.getLogger(RatingEventsGenerator.class.getName());
    private static final double BAD_SCORE_LIMIT = 2;
    
    @Inject
    RatingMessageService messageService;
    
    @Inject
    @Outbound(loopBack = true)
    Event<InstanceEventData> event;
    
    @Schedule(hour = "*", minute="*", second = "*/1", persistent = false)
    public void generateRating(){
        int score = ThreadLocalRandom.current().nextInt(1, 6);
        String message = getRandomMessage(score <= BAD_SCORE_LIMIT ? Reaction.BAD : Reaction.GOOD);
        RatingData data = new RatingData(score, message);
        LOG.log(Level.FINE, "Generated {0}", data);
        event.fire(new InstanceEventData(getInstanceName(), data));
    }
    
    private String getRandomMessage(Reaction reaction){
        List<String> messages = messageService.getMessagesByReaction(reaction);
        int position = ThreadLocalRandom.current().nextInt(messages.size()) % messages.size();
        return messages.get(position);
    }
    
    private static String getInstanceName(){
        String instanceName = null;
        try{
            instanceName = PayaraMicro.getInstance().getInstanceName();
        }catch(Exception exception){
            LOG.log(Level.SEVERE, "Error retrieving instance name", exception);            
        }
        return Optional.ofNullable(instanceName).orElse("payara-micro");
    }
}
