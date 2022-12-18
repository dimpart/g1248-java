package chat.dim.g1248;

import chat.dim.dbi.AccountDBI;
import chat.dim.dbi.MessageDBI;
import chat.dim.dbi.SessionDBI;

public enum GlobalVariable {

    INSTANCE;

    public static GlobalVariable getInstance() {
        return INSTANCE;
    }

    GlobalVariable() {
    }

    public AccountDBI adb;
    public MessageDBI mdb;
    public SessionDBI sdb;
}
