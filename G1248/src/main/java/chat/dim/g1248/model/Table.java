package chat.dim.g1248.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chat.dim.type.Dictionary;
import chat.dim.type.Mapper;

/**
 *  Game Table
 *  ~~~~~~~~~~
 *
 *  JSON: {
 *      tid    : {TABLE_ID},
 *      // current playing boards
 *      boards : [
 *          {
 *              bid    : {BOARD_ID},     // 0, 1, 2, 3
 *              player : "{PLAYER_ID}",  // current player
 *
 *              // details, will not show in hall
 *              gid    : {GAME_ID},      // game id
 *              score  : 10000,          // current sore
 *              state  : [               // current state
 *                  0, 1, 2, 4,
 *                  0, 1, 2, 4,
 *                  0, 1, 2, 4,
 *                  0, 1, 2, 4
 *              ],
 *              size   : "4*4"
 *          },
 *          //...
 *      ]
 *  }
 */
public class Table extends Dictionary {

    public Table(Map<String, Object> table) {
        super(table);
    }

    // create new table
    public Table() {
        super();
    }

    /**
     *  Get Table ID
     *
     * @return 0
     */
    public int getTid() {
        Object tid = get("tid");
        return tid == null ? 0 : ((Number) tid).intValue();
    }
    public void setTid(int tid) {
        put("tid", tid);
    }

    /**
     *  Current playing boards in this table
     *
     * @return boards
     */
    @SuppressWarnings("unchecked")
    public List<Board> getBoards() {
        Object value = get("boards");
        if (value == null) {
            return new ArrayList<>();
        }
        return Board.convertBoards((List<Object>) value);
    }
    public void setBoards(List<Board> boards) {
        put("boards", Board.revertBoards(boards));
    }

    /**
     *  Get best score
     *
     * @return the winner's score
     */
    public Score getBest() {
        return Score.parseScore(get("best"));
    }
    public void setBest(Score best) {
        put("best", best.toMap());
    }

    //
    //  Factory methods
    //
    @SuppressWarnings("unchecked")
    public static Table parseTable(Object table) {
        if (table == null) {
            return null;
        } else if (table instanceof Table) {
            return (Table) table;
        } else if (table instanceof Mapper) {
            table = ((Mapper) table).toMap();
        }
        return new Table((Map<String, Object>) table);
    }

    public static List<Table> convertTables(List<Object> array) {
        List<Table> tables = new ArrayList<>();
        Table value;
        for (Object item : array) {
            value = parseTable(item);
            assert value != null : "table error: " + item;
            tables.add(value);
        }
        return tables;
    }
    public static List<Object> revertTables(List<Table> tables) {
        List<Object> array = new ArrayList<>();
        for (Table item : tables) {
            assert item != null : "tables error: " + tables;
            array.add(item.toMap());
        }
        return array;
    }
}
