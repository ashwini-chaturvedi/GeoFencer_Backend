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
@ToString(exclude = "devices")//Exclude Including the devices in the tostring of the Admin Object
public class Admin {
    @Id
    private String emailId;  // Primary Key: it is not generated but given by the User

    private String fullName;
    private String userName;
    private String phoneNo;
    private String password;


    @Version
    private long version;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Device> devices = new ArrayList<>();

    /*
        Now, when you:
        save an Admin, the Devices in the list will also be saved.
        delete an Admin, all associated Devices will also be deleted.

    All Cascade Types included in CascadeType.ALL:
        CascadeType.PERSIST   // Saves the child when you save the parent
        CascadeType.MERGE     // Updates the child when you update the parent
        CascadeType.REMOVE    // Deletes the child when you delete the parent
        CascadeType.REFRESH   // Refreshes the child when you refresh the parent
        CascadeType.DETACH    // Detaches the child when you detach the parent
    */

    @JsonCreator
    public Admin(@JsonProperty("emailId") String emailId) {
        this.emailId = emailId;
    }
}
