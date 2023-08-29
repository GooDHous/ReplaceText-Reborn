package com.agentkosticka.replacetext.mixin;

import com.agentkosticka.replacetext.data.DataManager;
import com.agentkosticka.replacetext.shared.Methods;
import com.agentkosticka.replacetext.shared.Variables;
import com.agentkosticka.replacetext.stringpair.StringPair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class C2SPacketInterceptor {

    @Shadow protected abstract void feedbackAfterDownload(CompletableFuture<?> downloadFuture);

    @Inject(at = @At("HEAD"), method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V", cancellable = true)
    private void sendPacket(Packet<?> packet, CallbackInfo callbackInfo){
        if(packet instanceof ChatMessageC2SPacket){
            if(Variables.saltBypass.contains(((ChatMessageC2SPacket) packet).salt())){
                Variables.saltBypass.remove(((ChatMessageC2SPacket) packet).salt());
                return;
            }
            if (Variables.listeningToReplace && Variables.waitingWord != ""){
                Variables.listOfWordsToReplace.add(new StringPair(Variables.waitingWord, ((ChatMessageC2SPacket) packet).chatMessage()));
                Methods.SendMessage("Success! \""+Variables.waitingWord+"\" will be now replaced with \""+((ChatMessageC2SPacket) packet).chatMessage()+"\"");
                DataManager.saveData(Variables.listOfWordsToReplace);
                Variables.waitingWord = "";
                Variables.listeningToReplace = false;
                callbackInfo.cancel();
                return;
            }
            String packetMess = ((ChatMessageC2SPacket) packet).chatMessage();
            for (StringPair pair : Variables.listOfWordsToReplace){
                if(packetMess.contains(pair.first)){
                    packetMess = packetMess.replace(pair.first, pair.second);
                }
            }
            ChatMessageC2SPacket packet1 = (ChatMessageC2SPacket) packet;
            packet = new ChatMessageC2SPacket(packetMess, packet1.timestamp(), packet1.salt(), packet1.signature(), packet1.acknowledgment());
            if(packet != packet1){
                Variables.saltBypass.add(((ChatMessageC2SPacket) packet).salt());
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
                callbackInfo.cancel();
            }
        }
        if (packet instanceof CommandExecutionC2SPacket){
            if(Variables.saltBypass.contains(((CommandExecutionC2SPacket) packet).salt())){
                Variables.saltBypass.remove(((CommandExecutionC2SPacket) packet).salt());
                return;
            }
            String packetComm = ((CommandExecutionC2SPacket) packet).command();
            for (StringPair pair : Variables.listOfWordsToReplace){
                if(packetComm.contains(pair.first)){
                    packetComm = packetComm.replace(pair.first, pair.second);
                }
            }
            CommandExecutionC2SPacket packet1 = (CommandExecutionC2SPacket) packet;
            packet = new CommandExecutionC2SPacket(packetComm, packet1.timestamp(), packet1.salt(), packet1.argumentSignatures(), packet1.acknowledgment());
            if(packet != packet1){
                Variables.saltBypass.add(packet1.salt());
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
                callbackInfo.cancel();
            }
        }
    }
}
