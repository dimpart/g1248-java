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

    // If there is no action, the player will leave the table after 5 minutes.
    public static long EXPIRES = 300 * 1000;

    private final Map<Integer, Set<ID>> allTables = new HashMap<>();
    private final Map<ID, Long> onlineTimes = new HashMap<>();

    /**
     *  Add player in a table
     *
     * @param tid    - table id
     * @param player - player ID
     * @param now    - current time
     */
    public void addPlayer(int tid, ID player, long now) {
        Set<ID> table = allTables.get(tid);
        if (table == null) {
            table = new HashSet<>();
            table.add(player);
            allTables.put(tid, table);
        } else {
            table.add(player);
        }
        if (now == 0) {
            now = new Date().getTime();
        }
        // update online time
        onlineTimes.put(player, now);
    }

    /**
     *  Remove player from a table
     *
     * @param tid    - table id
     * @param player - player ID
     */
    public void removePlayer(int tid, ID player) {
        Set<ID> table = allTables.get(tid);
        if (table != null) {
            table.remove(player);
        }
        // clear online time
        onlineTimes.remove(player);
    }

    /**
     *  Check whether a player is on the table
     *
     * @param tid    - table id
     * @param player - player ID
     * @param now    - current time
     * @return true for watching/playing on this table
     */
    public boolean checkPlayer(int tid, ID player, long now) {
        if (now == 0) {
            now = new Date().getTime();
        }
        if (isExpired(player, now)) {
            // expired
            return false;
        }
        Set<ID> table = allTables.get(tid);
        return table != null && table.contains(player);
    }

    /**
     *  Get online players
     *
     * @param tid - table id
     * @param now - current time
     * @return online users
     */
    public Set<ID> getPlayers(int tid, long now) {
        Set<ID> onlineUsers = new HashSet<>();
        Set<ID> table = allTables.get(tid);
        if (table != null) {
            if (now == 0) {
                now = new Date().getTime();
            }
            for (ID item : table) {
                if (isExpired(item, now)) {
                    continue;
                }
                onlineUsers.add(item);
            }
        }
        return onlineUsers;
    }

    private boolean isExpired(ID player, long now) {
        long ts = lastTime(player);
        return 0 < ts && (ts + EXPIRES) < now;
    }
    private long lastTime(ID player) {
        Object ts = onlineTimes.get(player);
        return ts == null ? 0 : (long) ts;
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
        Set<ID> gamers = getPlayers(tid, 0);
        gamers.remove(player);
        Log.info("broadcast game content: " + player + " => " + gamers);
        for (ID member : gamers) {
            messenger.sendContent(null, member, content, 0);
        }
    }
}
