package chat.dim.g1248.protocol;

import java.util.Map;

/**
 *  Game Table Content
 *  ~~~~~~~~~~~~~~~~~~
 */
public class GameTableContent extends GameCustomizedContent {

    public static final String MOD_NAME = "table";

    // querying competitions on the table
    public static final String ACT_WATCH_REQ = "watching";
    public static final String ACT_WATCH_RES = "boards";

    // post next move on the board
    public static final String ACT_PLAY_REQ  = "playing";
    public static final String ACT_PLAY_RES  = "played";

    public GameTableContent(Map<String, Object> content) {
        super(content);
    }

    public GameTableContent(String act) {
        super(MOD_NAME, act);
    }

    /**
     *  Create game hall content
     *
     * @param act - action name
     * @return application customized content
     */
    public static GameTableContent create(String act) {
        return new GameTableContent(act);
    }
}
