package milansomyk.springboothw.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import milansomyk.springboothw.view.Views;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.swing.text.View;
import java.util.List;

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
    @JsonView(Views.LevelManagerAdmin.class)
    private boolean enabled;
    @JsonView(Views.LevelManagerAdmin.class)
    private String role;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_car",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "car_id")
    )
    private List<Car> cars;

    public User(Integer id, String username, String password, String email, Integer phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
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

    public boolean isEnabled() {
        return enabled;
    }
}
