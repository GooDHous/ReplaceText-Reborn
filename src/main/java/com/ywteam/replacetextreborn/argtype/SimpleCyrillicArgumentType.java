package com.ywteam.replacetextreborn.argtype;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ywteam.replacetextreborn.shared.Variables;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.Arrays;

public class SimpleCyrillicArgumentType implements ArgumentType<String> {
    
    private SimpleCyrillicArgumentType() {}
    
    public static SimpleCyrillicArgumentType simpleCyrillic() {
        return new SimpleCyrillicArgumentType();
    }
    
    public static String getSimpleCyrillic(final CommandContext<FabricClientCommandSource> context, final String name) {
        return context.getArgument(name, String.class);
    }
    
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String result = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return result;
    }
    
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        try {
            String current = builder.getRemaining();
            for (var pair : Variables.listOfWordsToReplace) {
                if (pair.first.toLowerCase().startsWith(current.toLowerCase())) {
                    builder.suggest(pair.first);
                }
            }
        } catch (Exception e) {
            // Ignore errors
        }
        return builder.buildFuture();
    }
    
    @Override
    public Collection<String> getExamples() {
        return Arrays.asList("слово", "тест", "пример");
    }
}