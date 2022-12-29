package chat.dim.g1248.dbi;

import java.util.List;

import chat.dim.g1248.model.Board;
import chat.dim.g1248.model.Score;
import chat.dim.g1248.model.Table;

/**
 *  Game Hall
 *  ~~~~~~~~~
 *
 *  JSON: {
 *      range  : "{start},{end}",  // count(tables) == end - start
 *      tables : [
 *          {
 *              tid    : {TABLE_ID},
 *              // current playing boards
 *              boards : [
 *                  {
 *                      bid    : {BOARD_ID},     // 0, 1, 2, 3
 *                      player : "{PLAYER_ID}",  // current player
 *                  },
 *                  //...
 *              ],
 *              // score of the winner in this table, may be null
 *              best   : {
 *                  bid    : {BOARD_ID},
 *                  gid    : {GAME_ID},      // game id
 *                  player : "{PLAYER_ID}",  // game player
 *                  score  : 10000,          // game sore
 *                  time   : {TIMESTAMP}
 *              }
 *          },
 *          //...
 *      ]
 *  }
 */
public interface HallDBI {

    List<Table> getTables(int start, int end);

    Table getTable(int tid);

    boolean updateTable(int tid, List<Board> boards, Score best);
}
