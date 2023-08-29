package com.agentkosticka.replacetext.argtype;

import com.agentkosticka.replacetext.shared.Variables;
import com.agentkosticka.replacetext.stringpair.StringPair;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.IntegerSuggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import org.apache.commons.lang3.builder.ToStringSummary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ListStringArgumentType implements ArgumentType<String> {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        final String text = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return text;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> customSuggestion = new ArrayList<>();
        for (StringPair pair : Variables.listOfWordsToReplace){
            customSuggestion.add(pair.first);
        }

        //return ArgumentType.super.listSuggestions(context, builder);
        return CommandSource.suggestMatching(customSuggestion, builder);
    }
}
