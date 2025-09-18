package com.ywteam.replacetextreborn.shared;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Methods {
    public static void SendMessage(String message) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal(message), false);
        }
    }
}