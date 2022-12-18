package chat.dim.g1248.dbi;

import java.util.List;

import chat.dim.g1248.model.Table;

public interface HallDBI {

    List<Table> getTables(int start, int end);
}
