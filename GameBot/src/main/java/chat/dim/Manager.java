package chat.dim;

import java.io.IOException;
import java.util.List;

import chat.dim.g1248.GlobalVariable;
import chat.dim.g1248.SharedDatabase;
import chat.dim.g1248.model.Table;
import chat.dim.sqlite.game.HallDatabase;

public class Manager {

    static final int MIN_TABLES = 5;

    public static void main(String[] args) throws IOException {
        GlobalVariable shared = Client.shared;
        String ini = Client.ini;

        // Step 1: load config
        Config config = shared.createConfig(ini);

        // Step 2: create database
        SharedDatabase db = Client.createDatabase(config);
        HallDatabase hall = (HallDatabase) db.hallDatabase;

        List<Table> tables = hall.getTables(0, 20);
        int count = tables == null ? 0 : tables.size();
        System.out.println(">>> Got " + count + " table(s)");
        int index = count;
        for (; index < MIN_TABLES; ++index) {
            System.out.println(">>> adding table " + index);
            hall.addTable(null, null);
        }
        System.out.println(">>> " + (index - count) + " table(s) added.");
    }
}
