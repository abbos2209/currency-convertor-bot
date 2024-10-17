package uz.app.services;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.app.utils.UserState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BotService extends TelegramLongPollingBot{
    static BotLogicServise botLogicServise = BotLogicServise.getBotLogicServise();
    SendMessage sendMessage = new SendMessage();
    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            botLogicServise.handleMessage(update);
        }
        else if (update.hasCallbackQuery()){
            botLogicServise.handleCallback(update);
        }
    }

    @Override
    public String getBotUsername() {
        return "t.me/convertation_uz_bot";
    }

    @Override
    public String getBotToken() {
        return "7326017595:AAGNBHUToaVI_8ZxBYDcuQ5WwEfti1K1k70";
    }

    private BotService(){

    }
    @SneakyThrows
    public void sendSms(SendMessage message){
        execute(message);
    }

    static BotService botService;
    public static BotService getBotService(){
        if (botService == null) botService = new BotService();
        return botService;
    }

}
