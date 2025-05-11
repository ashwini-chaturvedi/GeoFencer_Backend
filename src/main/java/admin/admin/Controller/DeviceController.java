package admin.admin.Controller;



import java.util.List;
import java.util.Optional;

import admin.admin.Service.AdminService;
import admin.admin.Service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import admin.admin.Entity.Device;
import admin.admin.Service.DeviceService;



@RestController
@CrossOrigin(origins = "*",methods = {RequestMethod.GET,RequestMethod.OPTIONS,RequestMethod.HEAD,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.PUT})
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private LocationController locationController;

    // Create
    @PostMapping("/{email}")
    public ResponseEntity<Device> addDevice(@PathVariable String email, @RequestBody Device device) {
        try {
            Device createDevice = deviceService.createDevice(email,device);

            return ResponseEntity.status(HttpStatus.CREATED).body(createDevice);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    // Read by ID
    @GetMapping("/{email}")
    public ResponseEntity<List<Device>> getDevicesByAdminEmail(@PathVariable String email) {
        try {
            List<Device> devices = deviceService.readAllByAdminEmail(email);
            if (devices.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<List<Device>> update(@PathVariable String email ,@RequestBody Device device) {
        try {

            List<Device> devices = deviceService.readAllByAdminEmail(email);
            if (devices.isEmpty()) {
                System.out.println("Empty");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Device targetDevice=devices.stream()
                    .filter(prevDevice->prevDevice.getDeviceId()==device.getDeviceId())
                    .findFirst().orElseThrow();

            int idx=devices.indexOf(targetDevice);//Index of the target Device


            targetDevice.setDeviceName(device.getDeviceName());

            //Updating the location with the specific device id
            locationController.updateLocation(device.getDeviceId(),device.getHomeLocation());
            locationController.updateLocation(device.getDeviceId(),device.getCurrentLocation());

            devices.set(idx,targetDevice);

            System.out.println("targetDevice:"+targetDevice);

            return ResponseEntity.ok(this.deviceService.update(devices));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDeviceById(@PathVariable long id) {
        try {
            System.out.println("Deleting----1");
            System.out.println(id);
            deviceService.deleteById(id);
            return ResponseEntity.ok("Device with ID " + id + " deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete Device with ID " + id);
        }
    }

}
