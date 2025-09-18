package com.ywteam.replacetextreborn;

import com.ywteam.replacetextreborn.commands.ReplaceTextCommand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class ReplaceTextReborn implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ReplaceTextCommand.register(dispatcher);
        });
    }
}