package milansomyk.springboothw.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Data
@NoArgsConstructor
@Entity
@EnableAutoConfiguration
@Table(name="currency", schema = "public")
public class CurrencyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String ccy;
    private String buy;
    private String sale;
    public CurrencyValue(Integer id, String ccy, String buy, String sale){
        this.id =id;
        this.ccy = ccy;
        this.buy = buy;
        this.sale = sale;
    }
    public CurrencyValue setBuy(String buy){
        this.buy = buy;
        return this;
    }
    public CurrencyValue setSale(String sale){
        this.sale = sale;
        return this;
    }
}
