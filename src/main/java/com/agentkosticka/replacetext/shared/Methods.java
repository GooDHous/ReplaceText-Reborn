package com.agentkosticka.replacetext.shared;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.sql.Struct;

public class Methods {
    public static void SendMessage(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        try {
            mc.inGameHud.getChatHud().addMessage(Text.literal("§6[Replace Text]§r "+message));
        }
        catch (Exception e) {}
    }
}
