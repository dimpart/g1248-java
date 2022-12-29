package chat.dim;

import java.io.IOException;

import chat.dim.core.Processor;
import chat.dim.database.CipherKeyDatabase;
import chat.dim.database.DocumentDatabase;
import chat.dim.database.GroupDatabase;
import chat.dim.database.MetaDatabase;
import chat.dim.database.PrivateKeyDatabase;
import chat.dim.database.UserDatabase;
import chat.dim.dbi.MessageDBI;
import chat.dim.dbi.SessionDBI;
import chat.dim.filesys.ExternalStorage;
import chat.dim.g1248.AppMessageProcessor;
import chat.dim.g1248.GlobalVariable;
import chat.dim.g1248.SharedDatabase;
import chat.dim.g1248.handler.HallHandler;
import chat.dim.g1248.handler.HistoryHandler;
import chat.dim.g1248.handler.TableHandler;
import chat.dim.network.ClientSession;
import chat.dim.protocol.ID;
import chat.dim.sqlite.DatabaseConnector;
import chat.dim.sqlite.game.HallDatabase;
import chat.dim.sqlite.game.HistoryDatabase;
import chat.dim.sqlite.game.TableDatabase;
import chat.dim.utils.Log;

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
    protected Processor createProcessor(CommonFacebook facebook, ClientMessenger messenger) {
        return new AppMessageProcessor(facebook, messenger);
    }

    @Override
    protected ClientMessenger createMessenger(ClientSession session, CommonFacebook facebook) {
        MessageDBI mdb = (MessageDBI) facebook.getDatabase();
        return new CompatibleMessenger(session, facebook, mdb);
    }

    static SharedDatabase createDatabase(Config config) {
        String rootDir = config.getDatabaseRoot();
        String pubDir = config.getDatabasePublic();
        String priDir = config.getDatabasePrivate();

        ExternalStorage.setRoot(rootDir);

        String adbPath = config.getString("sqlite", "account");
        String mdbPath = config.getString("sqlite", "message");
        //String sdbPath = config.getString("sqlite", "session");
        String gdbPath = config.getString("sqlite", "game");

        DatabaseConnector adb = new DatabaseConnector(adbPath);
        DatabaseConnector mdb = new DatabaseConnector(mdbPath);
        //DatabaseConnector sdb = new DatabaseConnector(sdbPath);
        DatabaseConnector gdb = new DatabaseConnector(gdbPath);

        SharedDatabase db = SharedDatabase.getInstance();
        db.privateKeyDatabase = new PrivateKeyDatabase(rootDir, pubDir, priDir, adb);
        db.metaDatabase = new MetaDatabase(rootDir, pubDir, priDir, adb);
        db.documentDatabase = new DocumentDatabase(rootDir, pubDir, priDir, adb);
        db.userDatabase = new UserDatabase(rootDir, pubDir, priDir, adb);
        db.groupDatabase = new GroupDatabase(rootDir, pubDir, priDir, adb);
        db.cipherKeyDatabase = new CipherKeyDatabase(rootDir, pubDir, priDir, mdb);

        db.hallDatabase = new HallDatabase(gdb);
        db.tableDatabase = new TableDatabase(gdb);
        db.historyDatabase = new HistoryDatabase(gdb);
        return db;
    }

    static final GlobalVariable shared = GlobalVariable.getInstance();
    static final String alias = "g1248";
    static final String ini = "/Users/moky/Documents/GitHub/dimgame/g1248-java/GameBot/src/main/resources/config.ini";

    public static void main(String[] args) throws IOException {

        Log.LEVEL = Log.DEBUG;

        // Step 1: load config
        Config config = shared.createConfig(ini);
        ID bid = config.getANS(alias);
        assert bid != null : "Bot ID not defined: " + alias;
        System.out.println("[GAME] Bot ID: " + bid);

        // Step 2: create database
        SharedDatabase db = createDatabase(config);
        shared.adb = db;
        shared.mdb = db;
        shared.sdb = db;

        // Step 3: create facebook
        CommonFacebook facebook = shared.createFacebook(db, bid);

        // Step 4: create terminal
        Terminal client = new Client(facebook, db);
        Thread thread = new Thread(client);
        thread.setDaemon(false);
        thread.start();
        shared.terminal = client;

        // Step 5: create customized content handlers
        shared.gameHallContentHandler = new HallHandler(db);
        shared.gameTableContentHandler = new TableHandler(db);
        shared.gameHistoryContentHandler = new HistoryHandler(db);

        // Step 6: connect to remote address
        String host = config.getStationHost();
        int port = config.getStationPort();
        client.connect(host, port);
    }
}
