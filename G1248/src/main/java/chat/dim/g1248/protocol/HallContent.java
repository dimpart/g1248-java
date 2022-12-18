package chat.dim.g1248.protocol;

import java.util.Map;

/**
 *  Game Hall
 *  ~~~~~~~~~
 */
public class HallContent extends GameContent {

    public static final String MOD_NAME = "hall";

    // querying game tables in the hall
    public static final String ACT_SEEK_REQ = "seeking";
    public static final String ACT_SEEK_RES = "sought";

    public HallContent(Map<String, Object> content) {
        super(content);
    }

    public HallContent(String act) {
        super(MOD_NAME, act);
    }

    /**
     *  Create game hall content
     *
     * @param act - action name
     * @return application customized content
     */
    public static HallContent create(String act) {
        return new HallContent(act);
    }
}
