package admin.admin.Service;

import java.util.List;
import java.util.Optional;

import admin.admin.Entity.Admin;
import admin.admin.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import admin.admin.Entity.Device;
import admin.admin.Library.DeviceLibrary;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class DeviceService {

    @Autowired
    private DeviceLibrary deviceLibrary;

    @Autowired
    private AdminRepository adminRepository;



    //CREATE
    public Device createDevice(String email, Device device) {
        try {
            // Find admin by email
            Admin adminOpt = adminRepository.findById(email).orElseThrow();

            device.setAdmin(adminOpt);
            if (device.getHomeLocation() != null) {
                device.getHomeLocation().setDevice(device);
            }

            if (device.getCurrentLocation() != null) {
                device.getCurrentLocation().setDevice(device);
            }



            return deviceLibrary.save(device);
        } catch (OptimisticLockingFailureException e) {
            throw new OptimisticLockingFailureException("The device has been modified by another user.");
        }
    }

    public List<Device> update(List<Device> device) {
        return deviceLibrary.saveAll(device);
    }
    public Device updateDevice(long id,Device device) {
       Device existingDevice= this.deviceLibrary.findById(id);
       existingDevice.setOutOfGeofence(device.isOutOfGeofence());
        return deviceLibrary.save(existingDevice);
    }

    public Device updateSingleDevice(Device device) {
        try {
            return deviceLibrary.save(device);
        } catch (OptimisticLockingFailureException e) {
            throw new OptimisticLockingFailureException("The device has been modified by another user.");
        }
    }

    //READ
    // Fetch all devices by admin email
    public List<Device> readAllByAdminEmail(String email) {
        return deviceLibrary.findAllByAdminEmail(email);
    }
    public Device readById(long id){
        return this.deviceLibrary.findById(id);
    }




    //DELETE

    public void deleteById(long id){
        Device device=this.deviceLibrary.findById(id);

        System.out.println(device);

        if(device.getHomeLocation()!=null){
            device.getHomeLocation().setDevice(null);
            device.setHomeLocation(null);
        }

        System.out.println(device);
        if(device.getCurrentLocation()!=null){
            device.getCurrentLocation().setDevice(null);
            device.setCurrentLocation(null);
        }

        if(device.getAdmin()!=null){
            device.getAdmin().setDevices(null);
            device.setAdmin(null);
        }

        this.deviceLibrary.delete(device);
    }
    
}
