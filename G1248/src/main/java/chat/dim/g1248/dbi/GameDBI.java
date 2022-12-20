package chat.dim.g1248.dbi;

import chat.dim.g1248.model.History;

public interface GameDBI {

    History getHistory(int gid);
}
