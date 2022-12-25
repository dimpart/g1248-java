package chat.dim.sqlite.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.dim.format.JSON;
import chat.dim.g1248.dbi.HallDBI;
import chat.dim.g1248.model.Board;
import chat.dim.g1248.model.Score;
import chat.dim.g1248.model.Table;
import chat.dim.sql.SQLConditions;
import chat.dim.sqlite.DataTableHandler;
import chat.dim.sqlite.DatabaseConnector;
import chat.dim.sqlite.ResultSetExtractor;

/**
 *  Game Hall Database
 *  ~~~~~~~~~~~~~~~~~~
 */
public class HallDatabase extends DataTableHandler implements HallDBI {

    private ResultSetExtractor<Table> extractor;

    public HallDatabase(DatabaseConnector sqliteConnector) {
        super(sqliteConnector);
        // lazy load
        extractor = null;
    }

    private boolean prepare() {
        if (extractor == null) {
            // create table if not exists
            String[] fields = {
                    "tid INTEGER PRIMARY KEY AUTOINCREMENT",
                    "boards TEXT",
                    "best TEXT",
            };
            if (!createTable("t_game_table", fields)) {
                // db error
                return false;
            }
            // prepare result set extractor
            extractor = (resultSet, index) -> {
                int tid = resultSet.getInt("tid");
                String boards = resultSet.getString("boards");
                String best = resultSet.getString("best");

                Map<String, Object> info = new HashMap<>();
                info.put("tid", tid);
                if (boards != null && boards.length() > 0) {
                    info.put("boards", JSON.decode(boards));
                }
                if (best != null && best.length() > 0) {
                    info.put("best", JSON.decode(best));
                }
                return new Table(info);
            };
        }
        return true;
    }

    @Override
    public List<Table> getTables(int start, int end) {
        if (!prepare()) {
            // db error
            return null;
        }
        String[] columns = {"tid", "boards", "best"};
        return select(columns, "t_game_table", null,
                null, null, null, end - start, start,
                extractor);
    }

    private Table getTable(int tid) {
        if (!prepare()) {
            // db error
            return null;
        }
        SQLConditions conditions = new SQLConditions();
        conditions.addCondition(null, "tid", "=", tid);

        String[] columns = {"tid", "boards", "best"};
        List<Table> results = select(columns, "t_game_table", conditions, extractor);
        // return first record only
        return results == null || results.size() == 0 ? null : results.get(0);
    }
    private boolean addTable(int tid, List<Board> boards, Score best) {
        String array = boards == null ? "[]" : JSON.encode(boards);
        String dict = best == null ? "{}" : JSON.encode(best);

        String[] columns = {"tid", "boards", "best"};
        Object[] values = {tid, array, dict};
        return insert("t_game_table", columns, values) > 0;
    }

    @Override
    public boolean updateTable(int tid, List<Board> boards, Score best) {
        Table old = getTable(tid);
        if (old == null) {
            // add as new one
            return addTable(tid, boards, best);
        }
        // old record exists, update it
        String array = boards == null ? "[]" : JSON.encode(boards);
        String dict = best == null ? "{}" : JSON.encode(best);

        SQLConditions conditions = new SQLConditions();
        conditions.addCondition(null, "tid", "=", tid);

        Map<String, Object> values = new HashMap<>();
        values.put("boards", array);
        values.put("best", dict);
        return update("t_game_table", values, conditions) > 0;
    }
}
