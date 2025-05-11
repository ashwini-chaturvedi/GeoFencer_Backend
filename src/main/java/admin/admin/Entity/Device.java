package admin.admin.Entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString(exclude = {"homeLocation","currentLocation"})
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long deviceId;

    private String uniqueId;

    private String deviceName;

    private long geofenceRadius;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "home_location_id")
    private Location homeLocation;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "current_location_id")
    private Location currentLocation;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "admin_email", referencedColumnName = "emailId")
    private Admin admin;
}