package uz.app.services;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class MarkupService {
    public ReplyKeyboardMarkup getMarkup(String[][] buttons){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List <KeyboardRow> keyboardRows = new ArrayList<>();
        for (String[] button : buttons) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (String s : button) {
                KeyboardButton keyboardButton = new KeyboardButton();
                keyboardButton.setText(s);
                keyboardRow.add(keyboardButton);
            }
                keyboardRows.add(keyboardRow);
        }
            replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
    static MarkupService markupService;
    public static MarkupService getInlineService(){
        if (markupService == null) markupService = new MarkupService();
        return markupService;
    }
}
