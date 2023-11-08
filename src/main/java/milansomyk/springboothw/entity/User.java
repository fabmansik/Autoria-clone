package milansomyk.springboothw.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@EnableAutoConfiguration
@Table(name="users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private Integer phone;
    private boolean premium;
    private boolean enabled;
    private String role;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_car",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "car_id")
    )
    private List<Car> cars;

    public User(Integer id, String username, String password, String email, Integer phone, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public User setPremium(boolean premium) {
        this.premium = premium;
        return this;
    }
    public boolean getPremium(){
        return this.premium;
    }

    public User setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    public User setRole(String role){
        this.role = role;
        return this;
    }
}
