package com.agentkosticka.replacetext;

import com.agentkosticka.replacetext.commands.ReplaceTextCommand;
import com.agentkosticka.replacetext.data.DataManager;
import com.agentkosticka.replacetext.shared.Variables;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ReplaceTextClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> ReplaceTextCommand.register(dispatcher));
        Variables.listOfWordsToReplace = DataManager.loadData();
    }
}
