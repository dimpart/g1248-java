package chat.dim;

import chat.dim.dbi.AccountDBI;

public class SharedFacebook extends CommonFacebook {

    public SharedFacebook(AccountDBI db) {
        super(db);
    }
}
