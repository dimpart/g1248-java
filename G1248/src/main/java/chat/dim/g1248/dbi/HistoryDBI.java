package chat.dim.g1248.dbi;

import chat.dim.g1248.model.History;

/**
 *  Game History
 *  ~~~~~~~~~~~~
 *
 *  JSON: {
 *      tid    : {TABLE_ID},
 *      bid    : {BOARD_ID},
 *      gid    : {GAME_ID},      // game id
 *      player : "{PLAYER_ID}",  // game player
 *      score  : 10000,          // game sore
 *      time   : {TIMESTAMP},
 *
 *      steps  : "BASE64",       // encoded steps
 *
 *      state  : [               // current state
 *          0, 1, 2, 4,
 *          0, 1, 2, 4,
 *          0, 1, 2, 4,
 *          0, 1, 2, 4
 *      ],
 *      size   : "4*4"
 *  }
 */
public interface HistoryDBI {

    History getHistory(int gid);

    boolean saveHistory(History history);
}
