package uz.app.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.app.services.BotLogicServise;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserState {
    String state;
    Long chatId;
    String text = "";
    String data;
    Double currencyUSD;
    Double currencyEUR;
    Double currencyRUB;
    Boolean hasConverion;
    String source;
    String target;
    Boolean hasAmount;
}
