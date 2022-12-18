package chat.dim.g1248.protocol;

import java.util.Map;

import chat.dim.dkd.AppCustomizedContent;
import chat.dim.protocol.ContentType;

public class GameContent extends AppCustomizedContent {

    public static final String APP_ID = "chat.dim.g1248";

    public GameContent(Map<String, Object> content) {
        super(content);
    }

    public GameContent(String mod, String act) {
        super(ContentType.APPLICATION, APP_ID, mod, act);
    }
}
