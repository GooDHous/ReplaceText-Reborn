package com.ywteam.replacetextreborn.shared;

import java.util.ArrayList;
import java.util.List;

import com.ywteam.replacetextreborn.data.DataManager;
import com.ywteam.replacetextreborn.stringpair.StringPair;

public class Variables {
    public static List<StringPair> listOfWordsToReplace = new ArrayList<>();
    public static String waitingWord = "";
    public static boolean listeningToReplace = false;
    public static List<String> messBypass = new ArrayList<>();
    
    static {
        loadReplacements();
    }
    
    public static void loadReplacements() {
        listOfWordsToReplace = DataManager.loadData();
    }
}