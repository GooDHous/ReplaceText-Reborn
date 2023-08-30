package com.agentkosticka.replacetext.mixin;

import com.agentkosticka.replacetext.data.DataManager;
import com.agentkosticka.replacetext.shared.Methods;
import com.agentkosticka.replacetext.shared.Variables;
import com.agentkosticka.replacetext.stringpair.StringPair;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class MessageCatchNSend {
    @Shadow public abstract boolean sendMessage(String chatText, boolean addToHistory);

    @Inject(at = @At("HEAD"), cancellable = true, method = "sendMessage")
    private void sendMessage(String chatText, boolean addToHistory, CallbackInfoReturnable<Boolean> cir){
        if(Variables.messBypass.contains(chatText)){
            return;
        }
        if (Variables.listeningToReplace && Variables.waitingWord != "") {
            if(Variables.waitingWord.contains(chatText) || chatText.contains(Variables.waitingWord)){
                Methods.SendMessage("§4Error! Your words \""+Variables.waitingWord+"\" and \""+chatText+"\" can't contain each other");
            }

            else{
                Variables.listOfWordsToReplace.add(new StringPair(Variables.waitingWord, chatText));
                Methods.SendMessage("Success! \"" + Variables.waitingWord + "\" will be now replaced with \"" + chatText + "\"");
                DataManager.saveData(Variables.listOfWordsToReplace);
            }

            Variables.waitingWord = "";
            Variables.listeningToReplace = false;

            cir.setReturnValue(true);
            cir.cancel();
            return;
        }
        String originalText = chatText;
        for (StringPair pair : Variables.listOfWordsToReplace){
            while(chatText.contains(pair.first)){
                chatText = chatText.replace(pair.first, pair.second);
            }
        }
        if(originalText != chatText && !chatText.contains("/replace")){
            sendMessage(chatText, addToHistory);
            Variables.messBypass.add(chatText);
            cir.setReturnValue(true);
            cir.cancel();
        }
        //Methods.SendMessage("Chat! Message: "+chatText+", ath: "+addToHistory);
    }
}
