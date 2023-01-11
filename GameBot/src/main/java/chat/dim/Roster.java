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
        final int rid;
        final int bid;
        final long time;
        Record(int roomId, int boardId, long now) {
            rid = roomId;
            bid = boardId;
            time = now;
        }
    }

    // If there is no action, the player will leave the room after 5 minutes.
    public static long EXPIRES = 300 * 1000;

    private final Map<Integer, Set<ID>> allRooms = new HashMap<>();
    private final Map<ID, Record> allRecords = new HashMap<>();

    /**
     *  Add player in a room
     *  (this action will remove it from old room if exists)
     *
     * @param rid    - room id
     * @param bid    - board id
     * @param player - player ID
     * @param now    - current time
     */
    public void addPlayer(int rid, int bid, ID player, long now) {
        // 1. check old record
        Record record = allRecords.get(player);
        if (record != null && record.rid != rid) {
            // remove from old room
            Set<ID> room = allRooms.get(record.rid);
            if (room != null) {
                room.remove(player);
            }
        }

        // 2. update record
        if (now <= 0) {
            now = new Date().getTime();
        }
        allRecords.put(player, new Record(rid, bid, now));

        // 3. add player into the room
        Set<ID> room = allRooms.get(rid);
        if (room == null) {
            room = new HashSet<>();
            room.add(player);
            allRooms.put(rid, room);
        } else {
            room.add(player);
        }
    }

    /**
     *  Check whether a player is on the board
     *
     * @param rid    - room id
     * @param bid    - board id
     * @param player - player ID
     * @param now    - current time
     * @return true for watching/playing on this room
     */
    public boolean checkPlayer(int rid, int bid, ID player, long now) {
        Record record = allRecords.get(player);
        if (record == null) {
            // record not found
            return false;
        } else if (record.rid != rid || record.bid != bid) {
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
     * @param rid - room id
     * @param now - current time
     * @return online users
     */
    private Set<ID> getPlayers(int rid, long now) {
        Set<ID> onlineUsers = new HashSet<>();
        Set<ID> room = allRooms.get(rid);
        if (room != null) {
            if (now <= 0) {
                now = new Date().getTime();
            }
            final long expired = now - EXPIRES;
            Record record;
            for (ID item : room) {
                record = allRecords.get(item);
                if (record == null || record.rid != rid) {
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
     *  Send message content to all gamers on this room except the player itself
     *
     * @param rid     - room id
     * @param player  - player ID
     * @param content - game content
     */
    public void broadcast(int rid, ID player, Content content) {
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
        Set<ID> gamers = getPlayers(rid, new Date().getTime());
        gamers.remove(player);
        Log.info("broadcast game content: " + player + " => " + gamers);
        for (ID member : gamers) {
            messenger.sendContent(null, member, content, 0);
        }
    }
}
