package fish.payara.demos.eventpropagation.common;

import java.time.LocalDateTime;

/**
 * Used to represent rating and scoring data.
 * @author Fabio Turizo
 */
public class RatingData implements ClusteredEventData{
    
    private static final long serialVersionUID = 1L;
    
    private int score;
    private LocalDateTime time;
    private String message;

    public RatingData() {
    }

    public RatingData(int score, String message) {
        this.score = score;
        this.message = message;
        this.time = LocalDateTime.now();
    }

    public int getScore() {
        return score;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "(" + "score=" + score + ", message=" + message + ", time=" + time + ')';
    }
}
