package milansomyk.springboothw.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import milansomyk.springboothw.enums.Currency;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@EnableAutoConfiguration
@ToString
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
    private boolean active;
    private Date creationDate;
    private String photo;

    public Car(String model, String producer, Integer year, Integer power,
               String type, String details, Integer runKm, double engineVolume,
               String color, String region, String place, String transmission,
               String gearbox, Integer price, String currencyName, String currencyValue, Integer checkCount,
               Integer watchesPerDay, Integer watchesPerWeek, Integer watchesPerMonth, boolean active, Date creationDate ,String photo) {
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
        this.active = active;
        this.creationDate = creationDate;
        this.photo = photo;
    }
    public void update(Car car){
        this.model = car.getModel();
        this.producer = car.getProducer();
        this.year = car.getYear();
        this.power = car.getPower();
        this.type = car.getType();
        this.details = car.getDetails();
        this.runKm = car.getRunKm();
        this.engineVolume = car.getEngineVolume();
        this.color = car.getColor();
        this.region = car.getRegion();
        this.place = car.getPlace();
        this.transmission = car.getTransmission();
        this.gearbox = car.getGearbox();
        this.price = car.getPrice();
        this.currencyName = car.getCurrencyName();
        this.currencyValue = car.getCurrencyValue();
        this.checkCount = car.getCheckCount();
        this.watchesPerDay = car.getWatchesPerDay();
        this.watchesPerWeek = car.getWatchesPerWeek();
        this.watchesPerMonth = car.getWatchesPerMonth();
        this.active = car.isActive();
        this.photo = car.getPhoto();
    }
    public Integer addCheckCount(){
        this.checkCount++;
        return checkCount;
    }
}
