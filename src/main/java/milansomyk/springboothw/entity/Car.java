package milansomyk.springboothw.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    private String model;
    private String producer;
    private Integer year;
    private Integer power;
    private String type;
    private String details;
    private Integer runKm;
    private double engineVolume;
    private String color;
    private String region;
    private String place;
    private String transmission;
    private String gearbox;
    private String photo;

    public Car(Integer id, String model, String producer, Integer year, Integer power, String type, String details, Integer runKm, double engineVolume, String color, String region, String place, String transmission, String gearbox, String photo) {
        this.id = id;
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
        this.photo = photo;
    }
}
