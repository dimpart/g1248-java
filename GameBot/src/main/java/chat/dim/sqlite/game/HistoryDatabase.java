package chat.dim.sqlite.game;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.dim.format.Hex;
import chat.dim.format.JSON;
import chat.dim.g1248.dbi.HistoryDBI;
import chat.dim.g1248.model.History;
import chat.dim.g1248.model.State;
import chat.dim.math.Size;
import chat.dim.protocol.ID;
import chat.dim.sql.SQLConditions;
import chat.dim.sqlite.DataTableHandler;
import chat.dim.sqlite.DatabaseConnector;
import chat.dim.sqlite.ResultSetExtractor;

/**
 *  Game History Database
 *  ~~~~~~~~~~~~~~~~~~~~~
 */
public class HistoryDatabase extends DataTableHandler implements HistoryDBI {

    private ResultSetExtractor<History> extractor;

    public HistoryDatabase(DatabaseConnector sqliteConnector) {
        super(sqliteConnector);
        // lazy load
        extractor = null;
    }

    private boolean prepare() {
        if (extractor == null) {
            // create table if not exists
            String[] fields = {
                    "gid INTEGER PRIMARY KEY AUTOINCREMENT",
                    "tid INT",
                    "bid INT",
                    "player VARCHAR(64)",
                    "score INT",
                    "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",
                    "steps TEXT",
                    "state VARCHAR(100)",
                    "size VARCHAR(5)",
            };
            if (!createTable("t_game_history", fields)) {
                // db error
                return false;
            }
            // prepare result set extractor
            extractor = (resultSet, index) -> {
                int gid = resultSet.getInt("gid");
                int tid = resultSet.getInt("tid");
                int bid = resultSet.getInt("bid");
                String player = resultSet.getString("player");
                int score = resultSet.getInt("score");
                Time time = resultSet.getTime("time");
                String steps = resultSet.getString("steps");
                String state = resultSet.getString("state");
                String size = resultSet.getString("size");

                Map<String, Object> info = new HashMap<>();
                info.put("gid", gid);
                info.put("tid", tid);
                info.put("bid", bid);
                info.put("player", ID.parse(player));
                info.put("score", score);
                info.put("time", time.getTime() / 1000.0f);
                info.put("steps", steps);
                info.put("state", JSON.decode(state));
                info.put("size", size);
                return new History(info);
            };
        }
        return true;
    }

    @Override
    public History getHistory(int gid) {
        if (!prepare()) {
            // db error
            return null;
        }
        SQLConditions conditions = new SQLConditions();
        conditions.addCondition(null, "gid", "=", gid);
        String[] columns = {"gid", "tid", "bid", "player", "score", "time", "steps", "state", "size"};
        List<History> histories = select(columns, "t_game_history", conditions, extractor);
        return histories == null || histories.size() == 0 ? null : histories.get(0);
    }

    private boolean addHistory(History history) {
        int tid = history.getTid();
        int bid = history.getBid();
        //int gid = history.getGid();
        ID player = history.getPlayer();
        int score = history.getScore();
        //Date time = history.getTime();
        byte[] steps = history.getSteps();
        State state = history.getState();
        Size size = history.getBoardSize();

        String pid = player == null ? "" : player.toString();
        String hex = Hex.encode(steps);
        List<Integer> squares = state.toArray();

        String[] columns = {"tid", "bid", "player", "score", "steps", "state", "size"};
        Object[] values = {tid, bid, pid, score, hex, squares, size};
        return insert("t_game_history", columns, values) > 0;
    }

    @Override
    public boolean saveHistory(History history) {
        if (history.getGid() <= 0) {
            // add as new one
            return addHistory(history);
        }
        // old record exists, update it
        int tid = history.getTid();
        int bid = history.getBid();
        int gid = history.getGid();
        ID player = history.getPlayer();
        int score = history.getScore();
        //Date time = history.getTime();
        byte[] steps = history.getSteps();
        State state = history.getState();
        Size size = history.getBoardSize();

        String pid = player == null ? "" : player.toString();
        String hex = Hex.encode(steps);
        List<Integer> squares = state.toArray();

        SQLConditions conditions = new SQLConditions();
        conditions.addCondition(null, "gid", "=", gid);

        Map<String, Object> values = new HashMap<>();
        values.put("tid", tid);
        values.put("bid", bid);
        values.put("player", pid);
        values.put("score", score);
        values.put("steps", hex);
        values.put("state", squares);
        values.put("size", size);
        return update("t_game_history", values, conditions) > 0;
    }
}
