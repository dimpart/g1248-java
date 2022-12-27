package chat.dim.g1248;

import java.io.IOException;
import java.util.List;

import chat.dim.CommonFacebook;
import chat.dim.Config;
import chat.dim.CryptoPlugins;
import chat.dim.Terminal;
import chat.dim.crypto.DecryptKey;
import chat.dim.crypto.SignKey;
import chat.dim.dbi.AccountDBI;
import chat.dim.dbi.MessageDBI;
import chat.dim.dbi.SessionDBI;
import chat.dim.filesys.ExternalStorage;
import chat.dim.g1248.handler.GameHallContentHandler;
import chat.dim.g1248.handler.GameTableContentHandler;
import chat.dim.http.HTTPClient;
import chat.dim.mkm.User;
import chat.dim.protocol.ID;

public enum GlobalVariable {

    INSTANCE;

    public static GlobalVariable getInstance() {
        return INSTANCE;
    }

    GlobalVariable() {

        CryptoPlugins.registerCryptoPlugins();

        // initialize all factories & plugins
        HTTPClient http = HTTPClient.getInstance();
        http.start();
    }

    // Step 1: load config
    public Config config = null;

    // Step 2: create databases
    public AccountDBI adb = null;
    public MessageDBI mdb = null;
    public SessionDBI sdb = null;

    // Step 3: create facebook
    public CommonFacebook facebook = null;

    // Step 4: create client
    public Terminal terminal = null;

    //
    //  game customized content handlers
    //
    public GameHallContentHandler gameHallContentHandler = null;
    public GameTableContentHandler gameTableContentHandler = null;

    //
    //  default creators
    //

    public Config createConfig(String iniFilePath) throws IOException {
        // load config from file
        String iniFileContent = ExternalStorage.loadText(iniFilePath);
        if (iniFileContent == null || iniFileContent.length() == 0) {
            throw new IOException("ini file empty: " + iniFilePath);
        }
        config = Config.load(iniFileContent);
        return config;
    }

    public CommonFacebook createFacebook(AccountDBI adb, ID currentUser) {
        facebook = new CommonFacebook(adb);
        // make sure private key exists
        SignKey signKey = facebook.getPrivateKeyForVisaSignature(currentUser);
        List<DecryptKey> msgKeys = facebook.getPrivateKeysForDecryption(currentUser);
        if (signKey == null || msgKeys == null || msgKeys.size() == 0) {
            throw new AssertionError("failed to get private keys for user: " + currentUser);
        }
        User user = facebook.getUser(currentUser);
        assert user != null : "failed to get current user: " + currentUser;
        facebook.setCurrentUser(user);
        return facebook;
    }
}
