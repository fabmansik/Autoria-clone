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
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String ccy;
    private String buy;
    private String sale;

    public Currency setBuy(String buy){
        this.buy = buy;
        return this;
    }
    public Currency setSale(String sale){
        this.sale = sale;
        return this;
    }
    public Currency setCcy(String ccy){
        this.ccy = ccy;
        return this;
    }
}
