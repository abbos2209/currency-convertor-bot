
package uz.app.currency;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Currency {

    private String Ccy;

    private String CcyNmEN;

    private String CcyNmRU;

    private String CcyNmUZ;

    private String CcyNmUZC;
    private String Code;
    private String Date;
    private String Diff;

    private Long Id;
    private String Nominal;
    private String Rate;

}
