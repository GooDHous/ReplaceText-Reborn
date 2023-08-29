package com.agentkosticka.replacetext.data;

import com.agentkosticka.replacetext.stringpair.StringPair;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static void saveData(List<StringPair> list){
        File gameDir = MinecraftClient.getInstance().runDirectory;
        File file = new File(gameDir, "replacetext.atd");

        try {
            // Clear the existing content by truncating the file
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            fileOutputStream.close();

            // Open the file for writing
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            // Write each string from the list on a new line
            for (StringPair stringPair : list) {
                writer.write(stringPair.first);
                writer.newLine(); // Move to the next line
                writer.write(stringPair.second);
                writer.newLine(); // Move to the next line
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<StringPair> loadData() {
        List<StringPair> list = new ArrayList<>();
        File gameDir = MinecraftClient.getInstance().runDirectory;
        File file = new File(gameDir, "replacetext.atd");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String first = line;
                String second = reader.readLine(); // Read the next line for the second string
                if (second != null) {
                    list.add(new StringPair(first, second));
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
