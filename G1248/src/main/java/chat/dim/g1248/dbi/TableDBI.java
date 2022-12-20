package chat.dim.g1248.dbi;

import java.util.List;

import chat.dim.g1248.model.Board;
import chat.dim.g1248.model.Score;

public interface TableDBI {

    List<Board> getBoards(int tid);

    Score getBestScore(int tid);
}
