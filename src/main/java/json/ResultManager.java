package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ResultManager {
    public static List<GameResults> readGameDataFromJSON(String filePath) {
        Gson gson = new Gson();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            Type playerListType = new TypeToken<List<GameResults>>() {
            }.getType();
            return gson.fromJson(br, playerListType);
        } catch (IOException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public static void saveGameDataToJSON(GameResults output) throws IOException {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();
        builder.setPrettyPrinting().serializeNulls();
        List<GameResults> resList = new ArrayList<>();
        try (FileReader reader = new FileReader("results.json")) {
            List<GameResults> sth = gson.fromJson(reader, ArrayList.class);
            resList.addAll(sth);
        } catch (FileNotFoundException e) {
        }
        resList.add(output);
        try (var writer = new FileWriter("results.json")) {
            writer.write(gson.toJson(resList));
        }
    }
}
