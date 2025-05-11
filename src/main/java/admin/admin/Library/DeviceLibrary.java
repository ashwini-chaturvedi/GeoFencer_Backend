package admin.admin.Library;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import admin.admin.Entity.Device;
import admin.admin.Repository.DeviceRepository;

@Component
public class DeviceLibrary {

    @Autowired
    private DeviceRepository repository;

    //CREATE
    public Device save(Device detail){
        return this.repository.save(detail);
    }

    public List<Device> saveAll(List<Device> devices) {
        Iterable<Device> savedDevices = this.repository.saveAll(devices);
        List<Device> result = new ArrayList<>();
        savedDevices.forEach(result::add);
        return result;
    }


    //READ
    // Fetch all devices by admin email
    public List<Device> findAllByAdminEmail(String email) {
        return repository.findAllByAdmin_EmailId(email);
    }
    public Device findById(long id){
        return this.repository.findByDeviceId(id);
    }



    //DELETE

    public void delete(Device device){
        System.out.println("Deleting----3");
        System.out.println(device);
        this.repository.delete(device);
    }  
}
