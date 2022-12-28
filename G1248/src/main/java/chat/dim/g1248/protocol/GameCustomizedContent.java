package chat.dim.g1248.protocol;

import java.util.Map;

import chat.dim.dkd.AppCustomizedContent;
import chat.dim.protocol.ContentType;

/**
 *  Application Customized Content
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  JSON: {
 *      type : 0xCC,
 *      sn   : 123,
 *
 *      app   : "chat.dim.g1248",
 *      mod   : "{MODULE}",        // "hall" or "table"
 *      act   : "{ACTION}",        // action name
 *      extra : info               // action parameters
 *  }
 */
public class GameCustomizedContent extends AppCustomizedContent {

    public static final String APP_ID = "chat.dim.g1248";

    public GameCustomizedContent(Map<String, Object> content) {
        super(content);
    }

    public GameCustomizedContent(String mod, String act) {
        super(ContentType.CUSTOMIZED, APP_ID, mod, act);
    }
}
