package com.ywteam.replacetextreborn.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ywteam.replacetextreborn.argtype.SimpleCyrillicArgumentType;
import com.ywteam.replacetextreborn.data.DataManager;
import com.ywteam.replacetextreborn.shared.Methods;
import com.ywteam.replacetextreborn.shared.Variables;
import com.ywteam.replacetextreborn.stringpair.StringPair;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;


import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class ReplaceTextCommand {
    private static final String MOD_VERSION = "0.1";
    private static final String MOD_AUTHOR = "Weh";
    private static final String MOD_GITHUB = "https://github.com/GooDHous/ReplaceText-Reborn";
    
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        dispatcher.register(
            literal("replace")
            .then(literal("remove")
                .then(argument("word", SimpleCyrillicArgumentType.simpleCyrillic())
                    .executes(context -> {
                        String phrase = SimpleCyrillicArgumentType.getSimpleCyrillic(context, "word");
                        return removeReplacement(phrase);
                    })
                )
                .executes(context -> {
                    Methods.SendMessage("§6Usage: §a/replace remove <word>");
                    Methods.SendMessage("§7Use TAB to see available words");
                    return Command.SINGLE_SUCCESS;
                })
            )
        );

        dispatcher.register(
            literal("replace")
            .then(literal("add")
                .then(argument("toReplace", StringArgumentType.greedyString())
                    .executes(context -> {
                        String phrase = StringArgumentType.getString(context, "toReplace");
                        return addReplacement(phrase);
                    })
                )
            )
        );

        dispatcher.register(
            literal("replace")
            .then(literal("list")
                .executes(context -> {
                    return listReplacements();
                })
            )
        );

        dispatcher.register(
            literal("replace")
            .then(literal("reload")
                .executes(context -> {
                    return reloadReplacements();
                })
            )
        );

        dispatcher.register(
            literal("replace")
            .then(literal("help")
                .executes(context -> {
                    return showHelp();
                })
            )
        );

        dispatcher.register(
            literal("replace")
            .then(literal("credits")
                .executes(context -> {
                    return showCredits();
                })
            )
        );


        dispatcher.register(
            literal("replace")
            .executes(context -> {
                Methods.SendMessage("§6ReplaceText §av" + MOD_VERSION + "§6 - Loaded: §a" + Variables.listOfWordsToReplace.size() + "§6 replacements");
                Methods.SendMessage("§7Use §a/replace help §7for commands");
                return Command.SINGLE_SUCCESS;
            })
        );

    }

    private static int addReplacement(String phrase) {
        if (phrase.trim().isEmpty()) {
            Methods.SendMessage("§4Error: Word cannot be empty");
            return Command.SINGLE_SUCCESS;
        }
        for (StringPair pair : Variables.listOfWordsToReplace) {
            if (pair.first.equals(phrase)) { 
                Methods.SendMessage("§4Error: Word \"" + phrase + "\" is already being replaced with \"" + pair.second + "\"");
                return Command.SINGLE_SUCCESS;
            }
        }

        Variables.waitingWord = phrase;
        Variables.listeningToReplace = true;
        Methods.SendMessage("Now type what to replace \"" + phrase + "\" with");
        return Command.SINGLE_SUCCESS;
    }

    private static int removeReplacement(String phrase) {
        if (Variables.listOfWordsToReplace.isEmpty()) {
            Methods.SendMessage("§6No replacements to remove");
            return Command.SINGLE_SUCCESS;
        }
        
        phrase = phrase.trim();

        for (int i = 0; i < Variables.listOfWordsToReplace.size(); i++) {
            StringPair pair = Variables.listOfWordsToReplace.get(i);
            if (pair.first.equals(phrase)) {
                return removeReplacementAtIndex(i, phrase, pair.second);
            }
        }
        
        for (int i = 0; i < Variables.listOfWordsToReplace.size(); i++) {
            StringPair pair = Variables.listOfWordsToReplace.get(i);
            if (pair.first.equalsIgnoreCase(phrase)) {
                return removeReplacementAtIndex(i, pair.first, pair.second);
            }
        }
        
        Methods.SendMessage("§4Error: Word \"" + phrase + "\" not found");
        return Command.SINGLE_SUCCESS;
    }

    private static int removeReplacementAtIndex(int index, String word, String replacement) {
        Variables.listOfWordsToReplace.remove(index);
        DataManager.saveData(Variables.listOfWordsToReplace);
        Methods.SendMessage("§aRemoved: \"" + word + "\" → \"" + replacement + "\"");
        Methods.SendMessage("§7Total: §a" + Variables.listOfWordsToReplace.size() + "§7 replacements");
        return Command.SINGLE_SUCCESS;
    }

    private static int listReplacements() {
        if (Variables.listOfWordsToReplace.isEmpty()) {
            Methods.SendMessage("§6No replacements configured");
            return Command.SINGLE_SUCCESS;
        }
        
        Methods.SendMessage("§6=== Replacements (§a" + Variables.listOfWordsToReplace.size() + "§6) ===");
        for (int i = 0; i < Variables.listOfWordsToReplace.size(); i++) {
            StringPair pair = Variables.listOfWordsToReplace.get(i);
            Methods.SendMessage("§e" + (i + 1) + ". §f" + pair.first + " §4→§r " + pair.second);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadReplacements() {
        int previous = Variables.listOfWordsToReplace.size();
        Variables.listOfWordsToReplace = DataManager.loadData();
        Methods.SendMessage("§aReloaded: §e" + previous + "§a → §a" + Variables.listOfWordsToReplace.size() + "§a words");
        return Command.SINGLE_SUCCESS;
    }

    private static int showHelp() {
        Methods.SendMessage("§6=== ReplaceText-Reborn Commands ===");
        Methods.SendMessage("§a/replace add <word> §7- Add word to replace");
        Methods.SendMessage("§a/replace remove <word> §7- Remove replacement §8(TAB)");
        Methods.SendMessage("§a/replace list §7- Show all replacements");
        Methods.SendMessage("§a/replace reload §7- Reload from config");
        Methods.SendMessage("§a/replace credits §7- Show mod info");
        Methods.SendMessage("§a/replace help §7- Show help");
        Methods.SendMessage("§6Version: §a" + MOD_VERSION);
        return Command.SINGLE_SUCCESS;
    }

    private static int showCredits() {
        Methods.SendMessage("§6=== ReplaceText-Reborn Credits ===");
        Methods.SendMessage("§aThis a TextReplace fork for modern Minecraft versions ");
        Methods.SendMessage("§aOriginal author: §fAgentCubical");
        Methods.SendMessage("§aFork author: §f" + MOD_AUTHOR);
        Methods.SendMessage("§aGitHub: §9" + MOD_GITHUB);
        return Command.SINGLE_SUCCESS;
    }
}