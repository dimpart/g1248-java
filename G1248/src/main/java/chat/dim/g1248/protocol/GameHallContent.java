package chat.dim.g1248.protocol;

import java.util.Map;

/**
 *  Game Hall Content
 *  ~~~~~~~~~~~~~~~~~
 */
public class GameHallContent extends GameCustomizedContent {

    public static final String MOD_NAME = "hall";

    // querying game tables in the hall
    public static final String ACT_SEEK_REQ = "seeking";
    public static final String ACT_SEEK_RES = "tables";

    public GameHallContent(Map<String, Object> content) {
        super(content);
    }

    public GameHallContent(String act) {
        super(MOD_NAME, act);
    }

    /**
     *  Create game hall content
     *
     * @param act - action name
     * @return application customized content
     */
    public static GameHallContent create(String act) {
        return new GameHallContent(act);
    }
}
