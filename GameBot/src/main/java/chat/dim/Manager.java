package chat.dim;

import java.io.IOException;
import java.util.List;

import chat.dim.g1248.GlobalVariable;
import chat.dim.g1248.SharedDatabase;
import chat.dim.g1248.model.Room;
import chat.dim.sqlite.game.HallDatabase;

public class Manager {

    static final int MIN_ROOMS = 5;

    public static void main(String[] args) throws IOException {
        GlobalVariable shared = Client.shared;
        String ini = Client.ini;

        // Step 1: load config
        Config config = shared.createConfig(ini);

        // Step 2: create database
        SharedDatabase db = Client.createDatabase(config);
        HallDatabase hall = (HallDatabase) db.hallDatabase;

        List<Room> rooms = hall.getRooms(0, 20);
        int count = rooms == null ? 0 : rooms.size();
        System.out.println(">>> Got " + count + " room(s)");
        int index = count;
        for (; index < MIN_ROOMS; ++index) {
            System.out.println(">>> adding room " + index);
            hall.addRoom(null, null);
        }
        System.out.println(">>> " + (index - count) + " room(s) added.");
    }
}
