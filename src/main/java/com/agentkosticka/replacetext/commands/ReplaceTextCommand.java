package com.agentkosticka.replacetext.commands;

import com.agentkosticka.replacetext.shared.Methods;
import com.agentkosticka.replacetext.shared.Variables;
import com.agentkosticka.replacetext.stringpair.StringPair;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
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
    }
}
