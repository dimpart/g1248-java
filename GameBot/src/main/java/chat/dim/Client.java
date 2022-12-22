package chat.dim;

import chat.dim.dbi.MessageDBI;
import chat.dim.dbi.SessionDBI;
import chat.dim.network.ClientSession;

import java.io.IOException;

public class Client extends Terminal {

    public Client(CommonFacebook barrack, SessionDBI sdb) {
        super(barrack, sdb);
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getVersionName() {
        return null;
    }

    @Override
    public String getSystemVersion() {
        return null;
    }

    @Override
    public String getSystemModel() {
        return null;
    }

    @Override
    public String getSystemDevice() {
        return null;
    }

    @Override
    public String getDeviceBrand() {
        return null;
    }

    @Override
    public String getDeviceBoard() {
        return null;
    }

    @Override
    public String getDeviceManufacturer() {
        return null;
    }

    @Override
    protected ClientMessenger createMessenger(ClientSession session, CommonFacebook facebook) {
        MessageDBI mdb = (MessageDBI) facebook.getDatabase();
        return new ClientMessenger(session, facebook, mdb);
    }

    public static void main(String[] args) throws IOException {
        GlobalVariable.startBot(
                "/Users/moky/Documents/GitHub/dimgame/g1248-java/GameBot/src/main/resources/config.ini",
                "g1248");
    }
}
