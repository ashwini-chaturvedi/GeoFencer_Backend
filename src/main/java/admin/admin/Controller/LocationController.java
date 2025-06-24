package admin.admin.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import admin.admin.Entity.Admin;
import admin.admin.Entity.Device;
import admin.admin.Service.DeviceService;
import admin.admin.Service.GoogleService;
import admin.admin.Service.EmailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import admin.admin.Entity.Location;
import admin.admin.Service.LocationService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.HEAD, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private GoogleService googleService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EmailService emailService;



    // Create a new location
    @PostMapping
    public ResponseEntity<Location> addLocation(@RequestBody Location location) {
        try {
            if (location == null || location.getDevice() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Location createdLocation = locationService.createLocation(location);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get home location by Device ID
    @GetMapping("/home/{deviceId}")
    public ResponseEntity<Map<String, Object>> getHomeLocationByDeviceId(@PathVariable long deviceId) {
        try {
            Location homeLocation = locationService.findByDeviceId(deviceId);
            if (homeLocation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("location", homeLocation);
            response.put("lastLocation", locationService.findCurrentLocationByDeviceId(deviceId));
            response.put("deviceName", homeLocation.getDevice().getDeviceName());
            response.put("geofenceRadius", homeLocation.getDevice().getGeofenceRadius());
            response.put("uniqueId", homeLocation.getDevice().getUniqueId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get current location by Device ID
    @GetMapping("/current/{deviceId}")
    public ResponseEntity<Location> getCurrentLocationByDeviceId(@PathVariable long deviceId) {
        try {
            Location currentLocation = locationService.findCurrentLocationByDeviceId(deviceId);
            if (currentLocation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(currentLocation);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update location for a device
    @PutMapping("/update")
    public ResponseEntity<Location> updateLocation(long deviceId, Location updatedLocation) {
        try {
            Device device = deviceService.readById(deviceId);
            if (device == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Location existingLocation = locationService.findCurrentLocationByDeviceId(deviceId);
            Location homeLocation = locationService.findByDeviceId(deviceId);
            if (existingLocation == null) {
                // Create new location if it doesn't exist
                updatedLocation.setDevice(device);
                updatedLocation.setDateTime(LocalDateTime.now());
                Location newLocation = locationService.createLocation(updatedLocation);
                return ResponseEntity.status(HttpStatus.CREATED).body(newLocation);
            }

            // Update only specific fields rather than replacing the entire object
            existingLocation.setLatitude(updatedLocation.getLatitude());
            existingLocation.setLongitude(updatedLocation.getLongitude());
            existingLocation.setDateTime(LocalDateTime.now());

            // Calculate distance if needed
            String distance = googleService.calculateDistance(homeLocation,existingLocation);
            existingLocation.setDistance(distance);

            Location result = locationService.updateLocation(existingLocation);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @MessageMapping("/update-location")
    @SendTo("/topic/location")
    public Location broadcastLiveLocation(@Payload String data ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(data);

            double latitude = jsonNode.get("latitude").asDouble();
            double longitude = jsonNode.get("longitude").asDouble();
            long deviceId = jsonNode.path("device").path("deviceId").asLong();

            Location existingLocation = locationService.findCurrentLocationByDeviceId(deviceId);
            Location homeLocation = locationService.findByDeviceId(deviceId);
            Device device=this.deviceService.readById(deviceId);

            if (existingLocation != null) {
                // Update existing location
                existingLocation.setLatitude(latitude);
                existingLocation.setLongitude(longitude);
                existingLocation.setDateTime(LocalDateTime.now());

                String distance = googleService.calculateDistance(homeLocation,existingLocation);
                existingLocation.setDistance(distance);

                if (!distance.equals("0.00") && Double.parseDouble(distance) > device.getGeofenceRadius()) {
                    if (!device.isOutOfGeofence()) {
                        // First breach, send email
                        Admin admin = device.getAdmin();

                        if ( admin.getEmailId() != null) {
                            boolean emailSent = emailService.sendGeofenceAlert(
                                    admin.getEmailId(),
                                    admin.getFullName(),
                                    device.getDeviceName(),
                                    existingLocation.getDateTime(),
                                    existingLocation.getLatitude(),
                                    existingLocation.getLongitude(),
                                    distance
                            );
                        }

                       device.setOutOfGeofence(true);
                        deviceService.updateDevice(device.getDeviceId(),device); // Add this method to persist the flag
                    }
                } else {
                    // Device is back inside geofence, reset the flag
                    if (device.isOutOfGeofence()) {
                        device.setOutOfGeofence(false);
                        deviceService.updateDevice(device.getDeviceId(),device);
                    }
                }


                locationService.updateLocation(existingLocation);
                return existingLocation;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Delete a location by entity
    @DeleteMapping
    public ResponseEntity<String> deleteLocation(@RequestBody Long locationId) {
        try {

            locationService.delete(locationId);
            return ResponseEntity.ok("Location deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete location.");
        }
    }

    // Delete a location by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteLocationById(@PathVariable long id) {
        try {
            locationService.deleteById(id);
            return ResponseEntity.ok("Location with ID " + id + " deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete location with ID " + id);
        }
    }
}