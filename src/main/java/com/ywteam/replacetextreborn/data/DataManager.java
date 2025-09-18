package com.ywteam.replacetextreborn.data;

import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.ywteam.replacetextreborn.stringpair.StringPair;

public class DataManager {
    private static final String CONFIG_FILE_NAME = "replacetext.csv";
    
    public static Path getConfigDir() {
        File gameDir = MinecraftClient.getInstance().runDirectory;
        Path configDir = gameDir.toPath().resolve("config");
        
        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return configDir;
    }
    
    public static Path getConfigFile() {
        return getConfigDir().resolve(CONFIG_FILE_NAME);
    }
    
    public static void saveData(List<StringPair> list) {
        Path configFile = getConfigFile();
        
        try {
            Files.createDirectories(configFile.getParent());
            BufferedWriter writer = Files.newBufferedWriter(configFile);
            
            for (StringPair pair : list) {
                writer.write(pair.first);
                writer.newLine();
                writer.write(pair.second);
                writer.newLine();
            }
            
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<StringPair> loadData() {
        Path configFile = getConfigFile();
        List<StringPair> list = new ArrayList<>();
        
        if (!Files.exists(configFile)) {
            return list;
        }
        
        try {
            BufferedReader reader = Files.newBufferedReader(configFile);
            String line;
            
            while ((line = reader.readLine()) != null) {
                String first = line.trim();
                String second = reader.readLine();
                
                if (second != null) {
                    second = second.trim();
                    if (!first.isEmpty() && !second.isEmpty()) {
                        list.add(new StringPair(first, second));
                    }
                }
            }
            
            reader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
            saveData(new ArrayList<>());
        }
        
        return list;
    }
}