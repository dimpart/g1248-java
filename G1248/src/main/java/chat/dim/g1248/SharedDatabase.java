package chat.dim.g1248;

import java.util.List;
import java.util.Set;

import chat.dim.crypto.DecryptKey;
import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.SymmetricKey;
import chat.dim.dbi.AccountDBI;
import chat.dim.dbi.CipherKeyDBI;
import chat.dim.dbi.DocumentDBI;
import chat.dim.dbi.GroupDBI;
import chat.dim.dbi.MessageDBI;
import chat.dim.dbi.MetaDBI;
import chat.dim.dbi.PrivateKeyDBI;
import chat.dim.dbi.SessionDBI;
import chat.dim.dbi.UserDBI;
import chat.dim.g1248.dbi.GameDBI;
import chat.dim.g1248.dbi.HallDBI;
import chat.dim.g1248.dbi.HistoryDBI;
import chat.dim.g1248.dbi.TableDBI;
import chat.dim.g1248.model.Board;
import chat.dim.g1248.model.History;
import chat.dim.g1248.model.Score;
import chat.dim.g1248.model.Table;
import chat.dim.protocol.Document;
import chat.dim.protocol.ID;
import chat.dim.protocol.LoginCommand;
import chat.dim.protocol.Meta;
import chat.dim.protocol.ReliableMessage;
import chat.dim.type.Pair;
import chat.dim.type.Triplet;

public enum SharedDatabase implements AccountDBI, MessageDBI, SessionDBI, GameDBI {

    INSTANCE;

    public static SharedDatabase getInstance() {
        return INSTANCE;
    }

    public PrivateKeyDBI privateKeyDatabase = null;
    public MetaDBI metaDatabase = null;
    public DocumentDBI documentDatabase = null;
    public UserDBI userDatabase = null;
    public GroupDBI groupDatabase = null;
    public CipherKeyDBI cipherKeyDatabase = null;

    public HallDBI hallDatabase = null;
    public TableDBI tableDatabase = null;
    public HistoryDBI historyDatabase = null;

    //
    //  Game DBI
    //

    @Override
    public List<Table> getTables(int start, int end) {
        return hallDatabase.getTables(start, end);
    }

    @Override
    public boolean updateTable(int tid, List<Board> boards, Score best) {
        return hallDatabase.updateTable(tid, boards, best);
    }

    @Override
    public List<Board> getBoards(int tid) {
        return tableDatabase.getBoards(tid);
    }

    @Override
    public boolean updateBoard(int tid, Board board) {
        return tableDatabase.updateBoard(tid, board);
    }

    @Override
    public History getHistory(int gid) {
        return historyDatabase.getHistory(gid);
    }

    @Override
    public boolean saveHistory(History history) {
        return historyDatabase.saveHistory(history);
    }

    //
    //  PrivateKey DBI
    //

    @Override
    public boolean savePrivateKey(PrivateKey key, String type, ID user) {
        return privateKeyDatabase.savePrivateKey(key, type, user);
    }

    @Override
    public List<DecryptKey> getPrivateKeysForDecryption(ID user) {
        return privateKeyDatabase.getPrivateKeysForDecryption(user);
    }

    @Override
    public PrivateKey getPrivateKeyForSignature(ID user) {
        return privateKeyDatabase.getPrivateKeyForSignature(user);
    }

    @Override
    public PrivateKey getPrivateKeyForVisaSignature(ID user) {
        return privateKeyDatabase.getPrivateKeyForVisaSignature(user);
    }

    //
    //  Meta DBI
    //

    @Override
    public boolean saveMeta(Meta meta, ID entity) {
        return metaDatabase.saveMeta(meta, entity);
    }

    @Override
    public Meta getMeta(ID entity) {
        return metaDatabase.getMeta(entity);
    }

    //
    //  Document DBI
    //

    @Override
    public boolean saveDocument(Document doc) {
        return documentDatabase.saveDocument(doc);
    }

    @Override
    public Document getDocument(ID entity, String type) {
        return documentDatabase.getDocument(entity, type);
    }

    //
    //  User DBI
    //

    @Override
    public List<ID> getLocalUsers() {
        return userDatabase.getLocalUsers();
    }

    @Override
    public boolean saveLocalUsers(List<ID> users) {
        return userDatabase.saveLocalUsers(users);
    }

    @Override
    public List<ID> getContacts(ID user) {
        return userDatabase.getContacts(user);
    }

    @Override
    public boolean saveContacts(List<ID> contacts, ID user) {
        return userDatabase.saveContacts(contacts, user);
    }

    //
    //  Group DBI
    //

    @Override
    public ID getFounder(ID group) {
        return groupDatabase.getFounder(group);
    }

    @Override
    public ID getOwner(ID group) {
        return groupDatabase.getOwner(group);
    }

    @Override
    public List<ID> getMembers(ID group) {
        return groupDatabase.getMembers(group);
    }

    @Override
    public boolean saveMembers(List<ID> members, ID group) {
        return groupDatabase.saveMembers(members, group);
    }

    @Override
    public List<ID> getAssistants(ID group) {
        return groupDatabase.getAssistants(group);
    }

    @Override
    public boolean saveAssistants(List<ID> bots, ID group) {
        return groupDatabase.saveAssistants(bots, group);
    }

    //
    //  CipherKey DBI
    //

    @Override
    public SymmetricKey getCipherKey(ID sender, ID receiver, boolean generate) {
        return cipherKeyDatabase.getCipherKey(sender, receiver, generate);
    }

    @Override
    public void cacheCipherKey(ID sender, ID receiver, SymmetricKey key) {
        cipherKeyDatabase.cacheCipherKey(sender, receiver, key);
    }

    //
    //  ReliableMessage DBI
    //

    @Override
    public Pair<List<ReliableMessage>, Integer> getReliableMessages(ID receiver, int start, int limit) {
        return null;
    }

    @Override
    public boolean cacheReliableMessage(ID receiver, ReliableMessage msg) {
        return false;
    }

    @Override
    public boolean removeReliableMessage(ID receiver, ReliableMessage msg) {
        return false;
    }

    //
    //  Login DBI
    //

    @Override
    public Pair<LoginCommand, ReliableMessage> getLoginCommandMessage(ID identifier) {
        return null;
    }

    @Override
    public boolean saveLoginCommandMessage(ID identifier, LoginCommand cmd, ReliableMessage msg) {
        return false;
    }

    //
    //  Provider DBI
    //

    @Override
    public Set<Triplet<String, Integer, ID>> allNeighbors() {
        return null;
    }

    @Override
    public ID getNeighbor(String ip, int port) {
        return null;
    }

    @Override
    public boolean addNeighbor(String ip, int port, ID station) {
        return false;
    }

    @Override
    public boolean removeNeighbor(String ip, int port) {
        return false;
    }
}
