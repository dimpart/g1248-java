package chat.dim.g1248.protocol;

import java.util.Iterator;
import java.util.Map;

import chat.dim.dkd.AppCustomizedContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.ContentType;
import chat.dim.protocol.CustomizedContent;

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

    public static Content createResponse(CustomizedContent request, String mod, String act) {
        Content res = new GameCustomizedContent(mod, act);
        Iterator<Entry<String, Object>> iterator = request.entrySet().iterator();
        Map.Entry<String, Object> entry;
        String key;
        while (iterator.hasNext()) {
            entry = iterator.next();
            key = entry.getKey();
            if (key == null || key.equals("type") || key.equals("sn") || key.equals("time")) {
                continue;
            } else if (key.equals("app") || key.equals("mod") || key.equals("act")) {
                continue;
            }
            res.put(key, entry.getValue());
        }
        return res;
    }
}
