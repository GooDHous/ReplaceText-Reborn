package com.ywteam.replacetextreborn;

import com.ywteam.replacetextreborn.commands.ReplaceTextCommand;
import com.ywteam.replacetextreborn.data.DataManager;
import com.ywteam.replacetextreborn.shared.Variables;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class ReplaceTextClientReborn implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> ReplaceTextCommand.register(dispatcher));
        Variables.listOfWordsToReplace = DataManager.loadData();
    }
}
