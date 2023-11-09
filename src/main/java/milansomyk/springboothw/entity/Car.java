package milansomyk.springboothw.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import milansomyk.springboothw.enums.Currency;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Data
@NoArgsConstructor
@Entity
@EnableAutoConfiguration
@Table(name = "cars", schema = "public")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String model;
    private String producer;
    private Integer year;
    private Integer power;
    private String type;
    private String details;
    private Integer runKm;
    private Double engineVolume;
    private String color;
    private String region;
    private String place;
    private String transmission;
    private String gearbox;
    private Integer price;
    private String currencyName;
    private String currencyValue;
    private Integer checkCount;
    private Integer watchesPerDay;
    private Integer watchesPerWeek;
    private Integer watchesPerMonth;
    private String photo;

    public Car(String model, String producer, Integer year, Integer power,
               String type, String details, Integer runKm, double engineVolume,
               String color, String region, String place, String transmission,
               String gearbox, Integer price, String currencyName, String currencyValue, Integer checkCount,
               Integer watchesPerDay, Integer watchesPerWeek, Integer watchesPerMonth ,String photo) {
        this.model = model;
        this.producer = producer;
        this.year = year;
        this.power = power;
        this.type = type;
        this.details = details;
        this.runKm = runKm;
        this.engineVolume = engineVolume;
        this.color = color;
        this.region = region;
        this.place = place;
        this.transmission = transmission;
        this.gearbox = gearbox;
        this.price = price;
        this.currencyName = currencyName;
        this.currencyValue = currencyValue;
        this.checkCount = checkCount;
        this.watchesPerDay = watchesPerDay;
        this.watchesPerWeek = watchesPerWeek;
        this.watchesPerMonth = watchesPerMonth;
        this.photo = photo;
    }
    public void update(String model, String producer, Integer year, Integer power,
                       String type, String details, Integer runKm, double engineVolume,
                       String color, String region, String place, String transmission,
                       String gearbox, Integer price, String currencyName, String currencyValue, Integer checkCount,
                       Integer watchesPerDay, Integer watchesPerWeek, Integer watchesPerMonth , String photo){
        this.model = model;
        this.producer = producer;
        this.year = year;
        this.power = power;
        this.type = type;
        this.details = details;
        this.runKm = runKm;
        this.engineVolume = engineVolume;
        this.color = color;
        this.region = region;
        this.place = place;
        this.transmission = transmission;
        this.gearbox = gearbox;
        this.price = price;
        this.currencyName = currencyName;
        this.currencyValue = currencyValue;
        this.checkCount = checkCount;
        this.watchesPerDay = watchesPerDay;
        this.watchesPerWeek = watchesPerWeek;
        this.watchesPerMonth = watchesPerMonth;
        this.photo = photo;
    }
}
