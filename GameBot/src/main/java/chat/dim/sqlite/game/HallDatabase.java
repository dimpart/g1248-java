package chat.dim.sqlite.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.dim.format.JSON;
import chat.dim.g1248.dbi.HallDBI;
import chat.dim.g1248.model.Board;
import chat.dim.g1248.model.Room;
import chat.dim.g1248.model.Score;
import chat.dim.sql.SQLConditions;
import chat.dim.sqlite.DataRowExtractor;
import chat.dim.sqlite.DataTableHandler;
import chat.dim.sqlite.DatabaseConnector;

/**
 *  Game Hall Database
 *  ~~~~~~~~~~~~~~~~~~
 */
public class HallDatabase extends DataTableHandler<Room> implements HallDBI {

    private DataRowExtractor<Room> extractor;

    public HallDatabase(DatabaseConnector sqliteConnector) {
        super(sqliteConnector);
        // lazy load
        extractor = null;
    }

    @Override
    protected DataRowExtractor<Room> getDataRowExtractor() {
        return extractor;
    }

    private boolean prepare() {
        if (extractor == null) {
            // create table if not exists
            String[] fields = {
                    "rid INTEGER PRIMARY KEY AUTOINCREMENT",
                    "boards TEXT",
                    "best TEXT",
            };
            if (!createTable(T_ROOM, fields)) {
                // db error
                return false;
            }
            // prepare result set extractor
            extractor = (resultSet, index) -> {
                int rid = resultSet.getInt("rid");
                String boards = resultSet.getString("boards");
                String best = resultSet.getString("best");

                Map<String, Object> info = new HashMap<>();
                info.put("rid", rid);
                if (boards != null && boards.length() > 0) {
                    info.put("boards", JSON.decode(boards));
                }
                if (best != null && best.length() > 0) {
                    info.put("best", JSON.decode(best));
                }
                return new Room(info);
            };
        }
        return true;
    }
    private static final String[] SELECT_COLUMNS = {"rid", "boards", "best"};
    private static final String[] INSERT_COLUMNS = {"boards", "best"};
    private static final String T_ROOM = "t_game_room";

    @Override
    public List<Room> getRooms(int start, int end) {
        if (!prepare()) {
            // db error
            return null;
        }

        return select(T_ROOM, SELECT_COLUMNS, null,
                null, null, null, end - start, start);
    }

    @Override
    public Room getRoom(int rid) {
        if (!prepare()) {
            // db error
            return null;
        }
        SQLConditions conditions = new SQLConditions();
        conditions.addCondition(null, "rid", "=", rid);

        List<Room> results = select(T_ROOM, SELECT_COLUMNS, conditions);
        // return first record only
        return results == null || results.size() == 0 ? null : results.get(0);
    }

    public boolean addRoom(List<Board> boards, Score best) {
        String array = boards == null ? "[]" : JSON.encode(boards);
        String dict = best == null ? "{}" : JSON.encode(best);

        Object[] values = {array, dict};
        return insert(T_ROOM, INSERT_COLUMNS, values) > 0;
    }

    @Override
    public boolean updateRoom(int rid, List<Board> boards, Score best) {
        String array = boards == null ? "[]" : JSON.encode(boards);
        String dict = best == null ? "{}" : JSON.encode(best);

        SQLConditions conditions = new SQLConditions();
        conditions.addCondition(null, "rid", "=", rid);

        Map<String, Object> values = new HashMap<>();
        values.put("boards", array);
        values.put("best", dict);
        return update(T_ROOM, values, conditions) > 0;
    }
}
