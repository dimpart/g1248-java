package chat.dim;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.dim.crypto.DecryptKey;
import chat.dim.crypto.SignKey;
import chat.dim.database.*;
import chat.dim.dbi.AccountDBI;
import chat.dim.dbi.MessageDBI;
import chat.dim.dbi.SessionDBI;
import chat.dim.filesys.ExternalStorage;
import chat.dim.http.HTTPClient;
import chat.dim.mkm.User;
import chat.dim.protocol.ID;
import chat.dim.sqlite.Database;

public enum GlobalVariable {

    INSTANCE;

    public static GlobalVariable getInstance() {
        return INSTANCE;
    }

    GlobalVariable() {

        // initialize all factories & plugins
        HTTPClient http = HTTPClient.getInstance();
        http.start();
    }

    public Config config;

    public AccountDBI adb;
    public MessageDBI mdb;
    public SessionDBI sdb;
    public SharedDatabase database;

    public SharedFacebook facebook;

    public Terminal terminal;

    //
    //  Initializations
    //

    // Step 1: load config
    private static Config createConfig(String iniFilePath, ID currentUser) throws IOException {
        // load config from file
        String iniFileContent = ExternalStorage.loadText(iniFilePath);
        if (iniFileContent == null || iniFileContent.length() == 0) {
            throw new IOException("ini file empty: " + iniFilePath);
        }
        Config config = Config.load(iniFileContent);
        if (currentUser != null) {
            // set bot ID into config['bot']['id']
            Map<String, String> bot = config.getItems("bot");
            if (bot == null) {
                bot = new HashMap<>();
                config.setItems("bot", bot);
            }
            bot.put("id", currentUser.toString());
        }
        return config;
    }

    // Step 2: create database
    private static SharedDatabase createDatabase(Config config) {
        String rootDir = config.getDatabaseRoot();
        String pubDir = config.getDatabasePublic();
        String priDir = config.getDatabasePrivate();

        ExternalStorage.setRoot(rootDir);

        String adbPath = config.getString("sqlite", "account");
        String mdbPath = config.getString("sqlite", "message");
        //String sdbPath = config.getString("sqlite", "session");

        Database adb = new Database(adbPath);
        Database mdb = new Database(mdbPath);
        //Database sdb = new Database(sdbPath);

        SharedDatabase db = new SharedDatabase();
        db.privateKeyDatabase = new PrivateKeyDatabase(rootDir, pubDir, priDir, adb);
        db.metaDatabase = new MetaDatabase(rootDir, pubDir, priDir, adb);
        db.documentDatabase = new DocumentDatabase(rootDir, pubDir, priDir, adb);
        db.userDatabase = new UserDatabase(rootDir, pubDir, priDir, adb);
        db.groupDatabase = new GroupDatabase(rootDir, pubDir, priDir, adb);
        db.cipherKeyDatabase = new CipherKeyDatabase(rootDir, pubDir, priDir, mdb);
        return db;
    }

    // Step 3: create facebook
    private static SharedFacebook createFacebook(AccountDBI adb, ID currentUser) {
        SharedFacebook facebook = new SharedFacebook(adb);
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

    private static boolean checkBotId(Config config, String ansName) {
        ID identifier = config.getID("bot", "id");
        if (identifier != null) {
            // got it
            return true;
        }
        identifier = config.getID("ans", ansName);
        if (identifier == null) {
            // failed to get Bot ID
            return false;
        }
        Map<String, String> section = config.getItems("bot");
        if (section == null) {
            section = new HashMap<>();
            config.setItems("bot", section);;
        }
        section.put("id", identifier.toString());
        return true;
    }
    public static Terminal startBot(String defaultConfig, String ansName) throws IOException {
        // create global variable
        GlobalVariable shared = GlobalVariable.getInstance();
        // Step 1: load config
        Config config = createConfig(defaultConfig, null);
        shared.config = config;
        if (!checkBotId(config, ansName)) {
            throw new AssertionError("failed to get Bot ID: " + config);
        }
        // Step 2: create database
        SharedDatabase db = createDatabase(config);
        shared.adb = db;
        shared.mdb = db;
        shared.sdb = db;
        shared.database = db;
        // Step 3: create facebook
        ID bid = config.getID("bot", "id");
        SharedFacebook facebook = createFacebook(db, bid);
        shared.facebook = facebook;
        // Step 4: create terminal & connect to remote address
        Terminal client = new Client(facebook, db);
        Thread thread = new Thread(client);
        thread.setDaemon(false);
        thread.start();
        String host = config.getStationHost();
        int port = config.getStationPort();
        client.connect(host, port);
        return client;
    }
}
