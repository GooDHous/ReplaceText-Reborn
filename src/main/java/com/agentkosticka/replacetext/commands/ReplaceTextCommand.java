package com.agentkosticka.replacetext.commands;

import com.agentkosticka.replacetext.argtype.ListStringArgumentType;
import com.agentkosticka.replacetext.data.DataManager;
import com.agentkosticka.replacetext.shared.Methods;
import com.agentkosticka.replacetext.shared.Variables;
import com.agentkosticka.replacetext.stringpair.StringPair;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.getActiveDispatcher;

public class ReplaceTextCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("replace")
                        .then(literal("add")
                                .then(argument("toReplace", greedyString())
                                        .executes(context -> {
                                            String phrase = getString(context, "toReplace");
                                            Variables.waitingWord = phrase;
                                            Variables.listeningToReplace = true;
                                            Methods.SendMessage("Now type what to replace \""+phrase+"\" with");
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
        );
        dispatcher.register(
                literal("replace")
                        .then(literal("list")
                                .executes(context -> {
                                    Methods.SendMessage("List of word replacements:");
                                    for (StringPair pair : Variables.listOfWordsToReplace) {
                                        Methods.SendMessage(pair.first+" §4->§r "+pair.second);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
        );
        dispatcher.register(
                literal("replace")
                        .then(literal("remove")
                                .then(argument("word", new ListStringArgumentType())
                                        .executes(context -> {
                                            String phrase = context.getArgument("word", String.class); // Use String.class here
                                            List<String> list = new ArrayList<>();
                                            String otherWord;
                                            for (StringPair stringPair : Variables.listOfWordsToReplace){
                                                list.add(stringPair.first);
                                            }
                                            if (list.contains(phrase)) {
                                                otherWord = Variables.listOfWordsToReplace.get(list.indexOf(phrase)).second;
                                                Variables.listOfWordsToReplace.remove(list.indexOf(phrase));
                                            } else {
                                                Methods.SendMessage("§4Error! Word \""+phrase+"\" not classified");
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            DataManager.saveData(Variables.listOfWordsToReplace);
                                            Methods.SendMessage("The word \""+phrase+"\" will no longer be replaced with \""+otherWord+"\"");
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
        );
    }
}
