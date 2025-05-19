package admin.admin.Entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "devices")
public class Admin {
    @Id
    private String emailId;  // Primary Key

    private String fullName;
    private String userName;
    private String phoneNo;
    private String password;


    @Version
    private long version;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Device> devices = new ArrayList<>();

    @JsonCreator
    public Admin(@JsonProperty("emailId") String emailId) {
        this.emailId = emailId;
    }
}
