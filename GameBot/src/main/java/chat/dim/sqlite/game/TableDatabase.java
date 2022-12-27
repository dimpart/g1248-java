package chat.dim.sqlite.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.dim.format.JSON;
import chat.dim.g1248.dbi.TableDBI;
import chat.dim.g1248.model.Board;
import chat.dim.g1248.model.Square;
import chat.dim.math.Size;
import chat.dim.protocol.ID;
import chat.dim.sql.SQLConditions;
import chat.dim.sqlite.DataRowExtractor;
import chat.dim.sqlite.DataTableHandler;
import chat.dim.sqlite.DatabaseConnector;

/**
 *  Game Table Database
 *  ~~~~~~~~~~~~~~~~~~~
 */
public class TableDatabase extends DataTableHandler<Board> implements TableDBI {

    private DataRowExtractor<Board> extractor;

    public TableDatabase(DatabaseConnector sqliteConnector) {
        super(sqliteConnector);
        // lazy load
        extractor = null;
    }

    @Override
    protected DataRowExtractor<Board> getDataRowExtractor() {
        return extractor;
    }

    private boolean prepare() {
        if (extractor == null) {
            // create table if not exists
            String[] fields = {
                    "tid INT",
                    "bid INT",
                    "gid INT",
                    "player VARCHAR(64)",
                    "score INT",
                    "state VARCHAR(100)",
                    "size VARCHAR(5)",
            };
            if (!createTable(T_BOARD, fields)) {
                // db error
                return false;
            }
            // prepare result set extractor
            extractor = (resultSet, index) -> {
                int tid = resultSet.getInt("tid");
                int bid = resultSet.getInt("bid");
                int gid = resultSet.getInt("gid");
                String player = resultSet.getString("player");
                int score = resultSet.getInt("score");
                String state = resultSet.getString("state");
                String size = resultSet.getString("size");

                Map<String, Object> info = new HashMap<>();
                info.put("tid", tid);
                info.put("bid", bid);
                info.put("gid", gid);
                if (player != null && player.length() > 0) {
                    info.put("player", ID.parse(player));
                }
                info.put("score", score);
                if (state != null && state.length() > 0) {
                    info.put("state", JSON.decode(state));
                }
                info.put("size", size);
                return new Board(info);
            };
        }
        return true;
    }
    private static final String[] SELECT_COLUMNS = {"tid", "bid", "gid", "player", "score", "state", "size"};
    private static final String[] INSERT_COLUMNS = {"tid", "bid", "gid", "player", "score", "state", "size"};
    private static final String T_BOARD = "t_game_board";

    @Override
    public List<Board> getBoards(int tid) {
        if (!prepare()) {
            // db error
            return null;
        }
        SQLConditions conditions = new SQLConditions();
        conditions.addCondition(null, "tid", "=", tid);
        return select(T_BOARD, SELECT_COLUMNS, conditions);
    }

    private Board getBoard(int tid, int bid) {
        if (!prepare()) {
            // db error
            return null;
        }
        SQLConditions conditions = new SQLConditions();
        conditions.addCondition(null, "tid", "=", tid);
        conditions.addCondition(SQLConditions.Relation.AND, "bid", "=", bid);

        List<Board> results = select(T_BOARD, SELECT_COLUMNS, conditions);
        // return first record only
        return results == null || results.size() == 0 ? null : results.get(0);
    }
    private boolean addBoard(int tid, Board board) {
        int bid = board.getBid();
        int gid = board.getGid();
        ID player = board.getPlayer();
        int score = board.getScore();
        List<Square> state = board.getState();
        Size size = board.getSize();

        String pid = player == null ? "" : player.toString();
        List<Integer> squares = Square.revert(state);

        Object[] values = {tid, bid, gid, pid, score, squares, size};
        return insert(T_BOARD, INSERT_COLUMNS, values) > 0;
    }

    @Override
    public boolean updateBoard(int tid, Board board) {
        Board old = getBoard(tid, board.getBid());
        if (old == null) {
            // add as new one
            return addBoard(tid, board);
        }
        // old record exists, update it
        int bid = board.getBid();
        int gid = board.getGid();
        ID player = board.getPlayer();
        int score = board.getScore();
        List<Square> state = board.getState();
        Size size = board.getSize();

        String pid = player == null ? "" : player.toString();
        List<Integer> squares = Square.revert(state);

        SQLConditions conditions = new SQLConditions();
        conditions.addCondition(null, "tid", "=", tid);
        conditions.addCondition(SQLConditions.Relation.AND, "bid", "=", bid);

        Map<String, Object> values = new HashMap<>();
        values.put("gid", gid);
        values.put("player", pid);
        values.put("score", score);
        values.put("state", squares);
        values.put("size", size);
        return update(T_BOARD, values, conditions) > 0;
    }
}
