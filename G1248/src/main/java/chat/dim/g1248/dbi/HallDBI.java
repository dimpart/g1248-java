package chat.dim.g1248.dbi;

import java.util.List;

import chat.dim.g1248.model.Board;
import chat.dim.g1248.model.Room;
import chat.dim.g1248.model.Score;

/**
 *  Game Hall
 *  ~~~~~~~~~
 *
 *  JSON: {
 *      range : "{start},{end}",  // count(rooms) == end - start
 *      rooms : [
 *          {
 *              rid    : {ROOM_ID},
 *              // current playing boards
 *              boards : [
 *                  {
 *                      bid    : {BOARD_ID},     // 0, 1, 2, 3
 *                      player : "{PLAYER_ID}",  // current player
 *                  },
 *                  //...
 *              ],
 *              // score of the winner in this room, may be null
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

    List<Room> getRooms(int start, int end);

    Room getRoom(int rid);

    boolean updateRoom(int rid, List<Board> boards, Score best);
}
