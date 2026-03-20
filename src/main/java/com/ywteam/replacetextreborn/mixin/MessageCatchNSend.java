package com.ywteam.replacetextreborn.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ywteam.replacetextreborn.data.DataManager;
import com.ywteam.replacetextreborn.shared.Methods;
import com.ywteam.replacetextreborn.shared.Variables;
import com.ywteam.replacetextreborn.stringpair.StringPair;

@Mixin(ChatScreen.class)
public abstract class MessageCatchNSend {
    
    @Shadow
    protected TextFieldWidget chatField;
    
    @Shadow
    public abstract void sendMessage(String chatText, boolean addToHistory);

    @Inject(at = @At("HEAD"), cancellable = true, method = "sendMessage")
    private void onSendMessage(String chatText, boolean addToHistory, CallbackInfo ci) {

        if (Variables.messBypass.contains(chatText)) {
            return;
        }

        if (Variables.listeningToReplace && !Variables.waitingWord.isEmpty()) {
            if (isReplaceCommandProtected(Variables.waitingWord, chatText)) {
                Methods.SendMessage("§4Error! Cannot add replacements that affect the '/replace' command");
                Variables.waitingWord = "";
                Variables.listeningToReplace = false;
                chatField.setText("");
                ci.cancel();
                return;
            }
            
            if (Variables.waitingWord.equalsIgnoreCase(chatText)) {
                Methods.SendMessage("§4Error! Your words \"" + Variables.waitingWord + "\" and \"" + chatText + "\" can't be the same");
            } else {
                if (willCauseInfiniteLoop(Variables.waitingWord, chatText)) {
                    Methods.SendMessage("§4Error! This replacement would cause an infinite loop");
                    Methods.SendMessage("§7The replacement contains the original word");
                } else {
                    Variables.listOfWordsToReplace.add(new StringPair(Variables.waitingWord, chatText));
                    Methods.SendMessage("Success! \"" + Variables.waitingWord + "\" will be now replaced with \"" + chatText + "\"");
                    DataManager.saveData(Variables.listOfWordsToReplace);
                }
            }
        
            Variables.waitingWord = "";
            Variables.listeningToReplace = false;
            chatField.setText("");
            ci.cancel();
            return;
        }
        
        String originalText = chatText;
        String modifiedText = chatText;
        
        boolean isCommand = chatText.startsWith("/");
        
        for (StringPair pair : Variables.listOfWordsToReplace) {
            if (isCommand) {
                if (pair.first.startsWith("/")) {
                    modifiedText = replaceIgnoreCaseSafely(modifiedText, pair.first, pair.second);
                }
            } else {
                if (!pair.first.startsWith("/")) {
                    modifiedText = replaceIgnoreCaseSafely(modifiedText, pair.first, pair.second);
                }
            }
            
            if (modifiedText.length() > 1000) {
                Methods.SendMessage("§4Error: Replacement would make message too long");
                return;
            }
        }
        

        if (!originalText.equals(modifiedText)) {
            sendMessage(modifiedText, addToHistory);
            Variables.messBypass.add(modifiedText);
            chatField.setText("");
            ci.cancel();
        }
    }
    

    private boolean isReplaceCommandProtected(String original, String replacement) {
        String replaceCommand = "/replace";
        
        if (original.toLowerCase().contains(replaceCommand)) {
            return true;
        }
        

        if (original.startsWith("/") && replaceCommand.startsWith(original.toLowerCase())) {
            return true;
        }
        
        if (replacement.contains(replaceCommand)) {
            return true;
        }
        
        if (original.startsWith("/") && replacement.startsWith("/") && 
            replaceCommand.startsWith(replacement.toLowerCase())) {
            return true;
        }
        
        return false;
    }
    
    private boolean willCauseInfiniteLoop(String original, String replacement) {
        String lowerOriginal = original.toLowerCase();
        String lowerReplacement = replacement.toLowerCase();
        return lowerReplacement.contains(lowerOriginal);
    }
    
    private String replaceIgnoreCaseSafely(String text, String target, String replacement) {
        if (text == null || target == null || replacement == null || target.isEmpty()) {
            return text;
        }
        
        if (!containsIgnoreCase(text, target)) {
            return text;
        }
        
        StringBuilder result = new StringBuilder();
        String lowerText = text.toLowerCase();
        String lowerTarget = target.toLowerCase();
        int textLength = text.length();
        int targetLength = target.length();
        int lastIndex = 0;
        int iterationLimit = 30;
        
        int index = lowerText.indexOf(lowerTarget);
        while (index >= 0 && iterationLimit > 0) {
            result.append(text, lastIndex, index);
            result.append(replacement);
            lastIndex = index + targetLength;
            index = lowerText.indexOf(lowerTarget, lastIndex);
            iterationLimit--;
        }
        
        if (lastIndex < textLength) {
            result.append(text, lastIndex, textLength);
        }
        
        return result.toString();
    }
    
    private boolean containsIgnoreCase(String text, String target) {
        if (text == null || target == null) return false;
        return text.toLowerCase().contains(target.toLowerCase());
    }
}