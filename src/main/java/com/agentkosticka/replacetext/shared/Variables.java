package com.agentkosticka.replacetext.shared;

import com.agentkosticka.replacetext.stringpair.StringPair;

import java.util.ArrayList;
import java.util.List;

public class Variables {
    public static boolean listeningToReplace = false;
    public static String waitingWord = "";
    public static List<String> messBypass = new ArrayList<>();
    public static List<StringPair> listOfWordsToReplace = new ArrayList<>();
}
