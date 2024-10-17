package uz.app.services;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.app.utils.InlineString;

import java.util.ArrayList;
import java.util.List;

public class InlineService {
    public InlineKeyboardMarkup getInlineKeyboard(InlineString[][] buttons){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (InlineString[] button : buttons) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (InlineString inlineString : button) {
                InlineKeyboardButton inlineButton = new InlineKeyboardButton();
                inlineButton.setText(inlineString.getText());
                inlineButton.setCallbackData(inlineString.getData());
                inlineButton.setSwitchInlineQuery("reply");
                row.add(inlineButton);
            }
            rows.add(row);
        }
            inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    static InlineService inlineService;
    public static InlineService getInlineService(){
        if (inlineService == null) inlineService = new InlineService();
        return inlineService;
    }
}
