package uz.app.services;

import com.fasterxml.jackson.databind.type.ArrayType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.glassfish.jersey.message.internal.Token;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.app.currency.Currency;
import uz.app.utils.InlineString;
import uz.app.utils.UserState;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class BotLogicServise {

    BotService botService = BotService.getBotService();
    List<UserState> userStates = new ArrayList<>();
    UserState currentUserState;
    SendMessage sendMessage = new SendMessage();
    InlineService inlineService = InlineService.getInlineService();
    MarkupService markupService = MarkupService.getInlineService();
    Gson gson = new Gson().newBuilder().create();
    Double result;

    public void handleMessage(Update update){

        Optional<UserState> optionalUserState = getUser(update.getMessage().getChatId());
        System.out.println(optionalUserState);
        if (optionalUserState.isEmpty()){
            UserState userState = new UserState("/start",update.getMessage().getChatId(),"","",0.,0.,0.,false,"","",false);
            userState.setChatId(update.getMessage().getChatId());
            userStates.add(userState);
            currentUserState = userState;
            currentUserState.setText(update.getMessage().getText());
        } else {
            currentUserState = optionalUserState.get();
            currentUserState.setText(update.getMessage().getText());
        }
        /*if (!currentUserState.getHasAmount() && currentUserState.getText().matches("^\\d+$")){
            sendMessage.setText("На какую валюту?");
            botService.sendSms(sendMessage);
        }*/
        if (currentUserState.getHasAmount()){
            currentUserState.setCurrencyUSD(getCurrencies("USD"));
            currentUserState.setCurrencyEUR(getCurrencies("EUR"));
            currentUserState.setCurrencyRUB(getCurrencies("RUB"));
            System.out.println(currentUserState.getText());
          result = Double.valueOf(currentUserState.getText());
          if (currentUserState.getSource().equalsIgnoreCase("UZS") && currentUserState.getTarget().equalsIgnoreCase("USD")){
              result = result / getCurrencies("USD");
              DecimalFormat decimalFormat = new DecimalFormat("###,###.##");
              decimalFormat.format(result);
              sendMessage.setText(decimalFormat.format(result) + " USD");
          }
            if (currentUserState.getSource().equalsIgnoreCase("USD") && currentUserState.getTarget().equalsIgnoreCase("UZS")){
                result = result * getCurrencies("USD");
                DecimalFormat decimalFormat = new DecimalFormat("0,000.00");
                sendMessage.setText(decimalFormat.format(result) + " UZS");
            }

          if (currentUserState.getSource().equalsIgnoreCase("USD") && currentUserState.getTarget().equalsIgnoreCase("EUR")){
              result = (result*currentUserState.getCurrencyUSD())/currentUserState.getCurrencyEUR();
              DecimalFormat decimalFormat = new DecimalFormat("#.00");
              sendMessage.setText(decimalFormat.format(result) + " EUR");
          }
            if (currentUserState.getSource().equalsIgnoreCase("EUR") && currentUserState.getTarget().equalsIgnoreCase("USD")){
                result = (result*currentUserState.getCurrencyEUR())/currentUserState.getCurrencyUSD();
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                sendMessage.setText(decimalFormat.format(result) + " USD");
            }
            if (currentUserState.getSource().equalsIgnoreCase("USD") && currentUserState.getTarget().equalsIgnoreCase("RUB")){
                result = (result*currentUserState.getCurrencyUSD())/currentUserState.getCurrencyRUB();
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                sendMessage.setText(decimalFormat.format(result) + " RUB");
            }
            if (currentUserState.getSource().equalsIgnoreCase("RUB") && currentUserState.getTarget().equalsIgnoreCase("USD")){
                result = (result*currentUserState.getCurrencyRUB())/currentUserState.getCurrencyUSD();
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                sendMessage.setText(decimalFormat.format(result) + " USD");
            }
            sendMessage.setChatId(currentUserState.getChatId());
            sendMessage.setReplyMarkup(markupService.getMarkup(new String[][]{{"В гланое меню"}}));
            currentUserState.setState("back");
            currentUserState.setHasAmount(false);
            botService.sendSms(sendMessage);
        }

        switch (currentUserState.getState()){
            case "back" -> {
                sendMessage.setText("Добро пожаловать!");
                sendMessage.setReplyMarkup(markupService.getMarkup(new String[][]{{"курс валют","конвертация"}}));
                sendMessage.setChatId(currentUserState.getChatId());
                botService.sendSms(sendMessage);
                currentUserState.setState("/start");
                return;
            }
        }

        switch (currentUserState.getText()){
            case "/start" -> {
                sendMessage.setText("Добро пожаловать!");
                sendMessage.setReplyMarkup(markupService.getMarkup(new String[][]{{"курс валют","конвертация"}}));
                sendMessage.setChatId(currentUserState.getChatId());
                botService.sendSms(sendMessage);
            }
            case "курс валют" -> {
                sendMessage.setText("Сегодняшный курс валют -> " + LocalDate.now().toString());
                ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                replyKeyboardRemove.setRemoveKeyboard(true);
                replyKeyboardRemove.setSelective(true);
                sendMessage.setChatId(currentUserState.getChatId());
                sendMessage.setReplyMarkup(replyKeyboardRemove);
                botService.sendSms(sendMessage);
                sendMessage.setText(getCurrencies());
                sendMessage.setReplyMarkup(markupService.getMarkup(new String[][]{{"Назад"}}));
                currentUserState.setState("back");
                sendMessage.setChatId(currentUserState.getChatId());
                botService.sendSms(sendMessage);
            }

            case "конвертация" -> {
                sendMessage.setText("С какой валюты?");
                sendMessage.setChatId(currentUserState.getChatId());
                sendMessage.setReplyMarkup(markupService.getMarkup(new String[][]{{"USD","EUR","RUB","UZS"}}));
                botService.sendSms(sendMessage);
                currentUserState.setHasConverion(true);
            }
            case "USD" -> {
                currentUserState.setCurrencyUSD(getCurrencies("USD"));
                if (currentUserState.getHasConverion()){
                    currentUserState.setSource("USD");
                    sendMessage.setText("На какую валюту?");
                    sendMessage.setChatId(currentUserState.getChatId());
                    sendMessage.setReplyMarkup(markupService.getMarkup(new String[][]{{"EUR","RUB","UZS"}}));
                    botService.sendSms(sendMessage);
                    currentUserState.setHasConverion(false);
                } else {
                    currentUserState.setTarget("USD");
                    ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                    replyKeyboardRemove.setRemoveKeyboard(true);
                    replyKeyboardRemove.setSelective(true);
                    sendMessage.setText("введите сумму в : " + currentUserState.getSource());
                    sendMessage.setChatId(currentUserState.getChatId());
                    sendMessage.setReplyMarkup(replyKeyboardRemove);
                    botService.sendSms(sendMessage);
                    currentUserState.setHasAmount(true);
                }
            }
            case "EUR" -> {
                currentUserState.setCurrencyUSD(getCurrencies("EUR"));
                if (currentUserState.getHasConverion()){
                    currentUserState.setSource("EUR");
                    sendMessage.setText("На какую валюту?");
                    sendMessage.setChatId(currentUserState.getChatId());
                    sendMessage.setReplyMarkup(markupService.getMarkup(new String[][]{{"USD","RUB"}}));
                    botService.sendSms(sendMessage);
                    currentUserState.setHasConverion(false);
                } else {
                    currentUserState.setTarget("EUR");
                    ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                    replyKeyboardRemove.setRemoveKeyboard(true);
                    replyKeyboardRemove.setSelective(true);
                    sendMessage.setText("введите сумму в : " + currentUserState.getSource());
                    sendMessage.setChatId(currentUserState.getChatId());
                    sendMessage.setReplyMarkup(replyKeyboardRemove);
                    botService.sendSms(sendMessage);
                    currentUserState.setHasConverion(false);
                    currentUserState.setHasAmount(true);
                }
            }
            case "RUB" -> {
                currentUserState.setCurrencyUSD(getCurrencies("RUB"));
                if (currentUserState.getHasConverion()){
                    currentUserState.setSource("RUB");
                    sendMessage.setText("На какую валюту?");
                    sendMessage.setChatId(currentUserState.getChatId());
                    sendMessage.setReplyMarkup(markupService.getMarkup(new String[][]{{"USD","EUR"}}));
                    botService.sendSms(sendMessage);
                    currentUserState.setHasConverion(false);
                } else {
                    currentUserState.setTarget("RUB");
                    ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                    replyKeyboardRemove.setRemoveKeyboard(true);
                    replyKeyboardRemove.setSelective(true);
                    sendMessage.setText("введите сумму в : " + currentUserState.getSource());
                    sendMessage.setChatId(currentUserState.getChatId());
                    sendMessage.setReplyMarkup(replyKeyboardRemove);
                    botService.sendSms(sendMessage);
                    currentUserState.setHasConverion(false);
                    currentUserState.setHasAmount(true);
                }
            }
            case "UZS" -> {
                if (currentUserState.getHasConverion()){
                    currentUserState.setSource("UZS");
                    sendMessage.setText("На какую валюту?");
                    sendMessage.setChatId(currentUserState.getChatId());
                    sendMessage.setReplyMarkup(markupService.getMarkup(new String[][]{{"USD","EUR","RUB"}}));
                    botService.sendSms(sendMessage);
                    currentUserState.setHasConverion(false);
                } else {
                    currentUserState.setTarget("UZS");
                    ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                    replyKeyboardRemove.setRemoveKeyboard(true);
                    replyKeyboardRemove.setSelective(true);
                    sendMessage.setText("введите сумму в : " + currentUserState.getSource());
                    sendMessage.setChatId(currentUserState.getChatId());
                    sendMessage.setReplyMarkup(replyKeyboardRemove);
                    botService.sendSms(sendMessage);
                    currentUserState.setHasConverion(false);
                    currentUserState.setHasAmount(true);
                }
            }


        }
    }
    public void handleCallback(Update update){

    }
    public Optional<UserState> getUser(Long id){
        for (UserState userState : userStates) {
            if (userState.getChatId().equals(id)){
                return Optional.of(userState);
            }
        }
        return Optional.empty();
    }


    private static BotLogicServise botLogicServise;
    public static BotLogicServise getBotLogicServise(){
        if (botLogicServise == null) botLogicServise = new BotLogicServise();
        return botLogicServise;
    }
    @SneakyThrows
    public String getCurrencies(){
        URL url = new URL("https://cbu.uz/ru/arkhiv-kursov-valyut/json/");
        URLConnection urlConnection = url.openConnection();
        InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
        Type type = new TypeToken<ArrayList<Currency>>(){}.getType();
        List<Currency> currencies = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()),type);
        StringBuilder sb = new StringBuilder();
        for (Currency temp : currencies) {
            if (temp.getCcy().equalsIgnoreCase("usd")){
                sb.append("1 USD💲 = " + temp.getRate()+ " UZS" + "\n");
                continue;
            }
            if (temp.getCcy().equalsIgnoreCase("eur")){
                sb.append("1 EUR € = " + temp.getRate()+ " UZS" + "\n");
                continue;
            }
            if (temp.getCcy().equalsIgnoreCase("rub")){
                sb.append("1 RUB ₽ = " + temp.getRate()+ " UZS" + "\n");
            }
        }
            return sb.toString();
//        BufferedReader bufferedReader = new BufferedReader(urlConnection.getInputStream());
    }

    @SneakyThrows
    public Double getCurrencies(String target){
        URL url = new URL("https://cbu.uz/ru/arkhiv-kursov-valyut/json/");
        URLConnection urlConnection = url.openConnection();
        InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
        Type type = new TypeToken<ArrayList<Currency>>(){}.getType();
        List<Currency> currencies = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()),type);
        StringBuilder sb = new StringBuilder();
        Double res = 0.;
        for (Currency temp : currencies) {
            if (temp.getCcy().equalsIgnoreCase(target)){
                res = Double.valueOf(temp.getRate());
                return res;
            }
        }
            return res;
        }

/*    public static void main(String[] args) throws IOException {
        URL url = new URL("https://cbu.uz/ru/arkhiv-kursov-valyut/json/");
        URLConnection urlConnection = url.openConnection();
        InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
        Type type = new TypeToken<ArrayList<Currency>>(){}.getType();
        List<Currency> currencies = new Gson().newBuilder().create().fromJson(new InputStreamReader(urlConnection.getInputStream()),type);
        System.out.println(currencies);
    }*/
}
