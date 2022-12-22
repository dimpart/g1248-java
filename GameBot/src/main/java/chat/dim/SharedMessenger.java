package chat.dim;

import chat.dim.core.Session;
import chat.dim.dbi.MessageDBI;

public class SharedMessenger extends ClientMessenger {

    public SharedMessenger(Session session, CommonFacebook facebook, MessageDBI database) {
        super(session, facebook, database);
    }
}
