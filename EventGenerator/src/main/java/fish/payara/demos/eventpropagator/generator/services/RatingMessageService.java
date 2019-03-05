package fish.payara.demos.eventpropagator.generator.services;

import fish.payara.demos.eventpropagator.generator.enumerations.Reaction;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author fabio
 */
@ApplicationScoped
public class RatingMessageService {

    private static final Logger LOG = Logger.getLogger(RatingMessageService.class.getName());
    private static final String DB_NAME = "/ratings-database.txt";

    private Map<Reaction, List<RatingDuple>> messageMap;

    private void loadMessages() {
        try (InputStream resource = getClass().getResourceAsStream(DB_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
            this.messageMap = reader.lines()
                  .map(line -> line.split(":"))
                  .map(RatingDuple::new)
                  .collect(Collectors.groupingBy(RatingDuple::getReaction));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
    @CacheResult
    public List<String> getMessagesByReaction(Reaction reaction){
        if(messageMap == null){
            loadMessages();
        }
        return messageMap.get(reaction)
                        .stream()
                        .map(RatingDuple::getMessage)
                        .collect(Collectors.toList());
    }
    
    private static class RatingDuple{
        private final Reaction reaction;
        private final String message;

        public RatingDuple(Reaction reaction, String message) {
            this.reaction = reaction;
            this.message = message;
        }

        public RatingDuple(String[] duple) {
            this(Reaction.valueOf(duple[0]), duple[1]);
        }

        public Reaction getReaction() {
            return reaction;
        }

        public String getMessage() {
            return message;
        }
        
    }
}
