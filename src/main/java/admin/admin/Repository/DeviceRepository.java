package admin.admin.Repository;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import admin.admin.Entity.Device;

public interface DeviceRepository extends CrudRepository<Device, Long> {


    //CREATE
    public Device save(Device detail);

    //READ
    // Fetch all devices by admin's emailId
    List<Device> findAllByAdmin_EmailId(String emailId);
    public Device findByDeviceId(long deviceId);


    

}

