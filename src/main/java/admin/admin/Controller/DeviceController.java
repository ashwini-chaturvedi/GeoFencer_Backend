package admin.admin.Controller;



import java.util.List;
import java.util.Objects;
import java.util.Optional;

import admin.admin.Entity.Location;
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
    public ResponseEntity<Device> updateDevice(@PathVariable String email, @RequestBody Device device) {
        try {

            // Get all devices for this admin
            List<Device> devices = deviceService.readAllByAdminEmail(email);
            if (devices.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Find the specific device to update
            Device targetDevice = devices.stream()
                    .filter(existingDevice -> Objects.equals(existingDevice.getDeviceId(), device.getDeviceId()))
                    .findFirst()
                    .orElse(null);

            if (targetDevice == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Update basic device properties
            if (device.getDeviceName() != null && !device.getDeviceName().trim().isEmpty()) {
                targetDevice.setDeviceName(device.getDeviceName().trim());
            }

            targetDevice.setGeofenceRadius(device.getGeofenceRadius());


            if (device.getUniqueId() != null) {
                targetDevice.setUniqueId(device.getUniqueId());
            }

            // Handle home location update
            if (device.getHomeLocation() != null) {
                if (targetDevice.getHomeLocation() != null) {

                    // Update existing home location
                    Location existingHomeLocation = targetDevice.getHomeLocation();
                    existingHomeLocation.setLatitude(device.getHomeLocation().getLatitude());
                    existingHomeLocation.setLongitude(device.getHomeLocation().getLongitude());
                    existingHomeLocation.setDistance(device.getHomeLocation().getDistance());
                    if (device.getHomeLocation().getDateTime() != null) {
                        existingHomeLocation.setDateTime(device.getHomeLocation().getDateTime());
                    }
                    // Update via LocationController
                    locationController.updateLocation(targetDevice.getDeviceId(), existingHomeLocation);
                } else {
                    // Create new home location
                    device.getHomeLocation().setDevice(targetDevice);
                    targetDevice.setHomeLocation(device.getHomeLocation());
                    locationController.addLocation(device.getHomeLocation());
                }
            }

            // Handle current location update
            if (device.getCurrentLocation() != null) {
                if (targetDevice.getCurrentLocation() != null) {
                    // Update existing current location
                    Location existingCurrentLocation = targetDevice.getCurrentLocation();
                    existingCurrentLocation.setLatitude(device.getCurrentLocation().getLatitude());
                    existingCurrentLocation.setLongitude(device.getCurrentLocation().getLongitude());
                    existingCurrentLocation.setDistance(device.getCurrentLocation().getDistance());
                    if (device.getCurrentLocation().getDateTime() != null) {
                        existingCurrentLocation.setDateTime(device.getCurrentLocation().getDateTime());
                    }
                    // Update via LocationController
                    locationController.updateLocation(targetDevice.getDeviceId(), existingCurrentLocation);
                } else {
                    // Create new current location
                    device.getCurrentLocation().setDevice(targetDevice);
                    targetDevice.setCurrentLocation(device.getCurrentLocation());
                    locationController.addLocation(device.getCurrentLocation());
                }
            }

            // Save the updated device
            Device updatedDevice = deviceService.updateSingleDevice(targetDevice);

            return ResponseEntity.ok(updatedDevice);

        } catch (Exception e) {
            System.err.println("Error updating device: " + e.getMessage());
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
