package chat.dim;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import chat.dim.g1248.GlobalVariable;
import chat.dim.network.ClientSession;
import chat.dim.network.SessionState;
import chat.dim.protocol.Content;
import chat.dim.protocol.ID;
import chat.dim.utils.Log;

public enum Roster {

    INSTANCE;

    public static Roster getInstance() {
        return INSTANCE;
    }

    static class Record {
        final int tid;
        final int bid;
        final long time;
        Record(int tableId, int boardId, long now) {
            tid = tableId;
            bid = boardId;
            time = now;
        }
    }

    // If there is no action, the player will leave the table after 5 minutes.
    public static long EXPIRES = 300 * 1000;

    private final Map<Integer, Set<ID>> allTables = new HashMap<>();
    private final Map<ID, Record> allRecords = new HashMap<>();

    /**
     *  Add player in a table
     *  (this action will remove it from old table if exists)
     *
     * @param tid    - table id
     * @param bid    - board id
     * @param player - player ID
     * @param now    - current time
     */
    public void addPlayer(int tid, int bid, ID player, long now) {
        // 1. check old record
        Record record = allRecords.get(player);
        if (record != null && record.tid != tid) {
            // remove from old table
            Set<ID> table = allTables.get(record.tid);
            if (table != null) {
                table.remove(player);
            }
        }

        // 2. update record
        if (now <= 0) {
            now = new Date().getTime();
        }
        allRecords.put(player, new Record(tid, bid, now));

        // 3. add player into the table
        Set<ID> table = allTables.get(tid);
        if (table == null) {
            table = new HashSet<>();
            table.add(player);
            allTables.put(tid, table);
        } else {
            table.add(player);
        }
    }

    /**
     *  Check whether a player is on the board
     *
     * @param tid    - table id
     * @param bid    - board id
     * @param player - player ID
     * @param now    - current time
     * @return true for watching/playing on this table
     */
    public boolean checkPlayer(int tid, int bid, ID player, long now) {
        Record record = allRecords.get(player);
        if (record == null) {
            // record not found
            return false;
        } else if (record.tid != tid || record.bid != bid) {
            // record not match
            return false;
        }
        // check last active time
        if (now <= 0) {
            now = new Date().getTime();
        }
        final long expired = now - EXPIRES;
        return record.time < expired;
    }

    /**
     *  Get online players
     *
     * @param tid - table id
     * @param now - current time
     * @return online users
     */
    private Set<ID> getPlayers(int tid, long now) {
        Set<ID> onlineUsers = new HashSet<>();
        Set<ID> table = allTables.get(tid);
        if (table != null) {
            if (now <= 0) {
                now = new Date().getTime();
            }
            final long expired = now - EXPIRES;
            Record record;
            for (ID item : table) {
                record = allRecords.get(item);
                if (record == null || record.tid != tid) {
                    // record not match
                    continue;
                } else if (record.time > expired) {
                    // record expired
                    continue;
                }
                onlineUsers.add(item);
            }
        }
        return onlineUsers;
    }

    /**
     *  Send message content to all gamers on this table except the player itself
     *
     * @param tid     - table id
     * @param player  - player ID
     * @param content - game content
     */
    public void broadcast(int tid, ID player, Content content) {
        GlobalVariable shared = GlobalVariable.getInstance();
        Terminal client = shared.terminal;
        SessionState sessionState = client.getState();
        ClientMessenger messenger = client.getMessenger();
        if (sessionState == null || messenger == null) {
            // not connect
            return;
        }
        ClientSession session = messenger.getSession();
        ID uid = session.getIdentifier();
        if (uid == null || !sessionState.equals(SessionState.RUNNING)) {
            // handshake not accepted
            return;
        }
        Set<ID> gamers = getPlayers(tid, new Date().getTime());
        gamers.remove(player);
        Log.info("broadcast game content: " + player + " => " + gamers);
        for (ID member : gamers) {
            messenger.sendContent(null, member, content, 0);
        }
    }
}
