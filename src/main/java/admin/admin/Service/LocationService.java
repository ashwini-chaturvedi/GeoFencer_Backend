package admin.admin.Service;

import admin.admin.Entity.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import admin.admin.Entity.Location;
import admin.admin.Library.LocationLibrary;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class LocationService {

    @Autowired
    private LocationLibrary locationLibrary;

    // CREATE
    public Location createLocation(Location detail) {
        if (detail == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        return this.locationLibrary.save(detail);
    }

    // READ - Home Location
    public Location findByDeviceId(long deviceId) {
        // Try native query first (most reliable)
        Location homeLocation = this.locationLibrary.findHomeLocationByDeviceIdNative(deviceId);

        // If still not found, try JPQL
        if (homeLocation == null) {
            homeLocation = this.locationLibrary.findHomeLocationByDeviceId(deviceId);
        }

        // If still not found, try by device association
        if (homeLocation == null) {
            homeLocation = this.locationLibrary.findByDeviceId(deviceId);
        }

        return homeLocation;
    }

    // READ - Current Location
    public Location findCurrentLocationByDeviceId(long deviceId) {
        // Try native query first (most reliable)
        Location currentLocation = this.locationLibrary.findCurrentLocationByDeviceIdNative(deviceId);

        // If still not found, try JPQL
        if (currentLocation == null) {
            currentLocation = this.locationLibrary.findCurrentLocationByDeviceId(deviceId);
        }

        return currentLocation;
    }

    // UPDATE
    public Location updateLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        return this.locationLibrary.save(location);
    }

    // DELETE
    public void delete(Long locationId) {
       Location location =this.locationLibrary.findById(locationId);

        Device device=location.getDevice();
        if(device!=null){
            if (device.getHomeLocation() != null && device.getHomeLocation().getId() == locationId) {
                device.setHomeLocation(null);
            }
            if (device.getCurrentLocation() != null && device.getCurrentLocation().getId() == locationId) {
                device.setCurrentLocation(null);
            }
            location.setDevice(null);
        }
        this.locationLibrary.delete(location);
    }

    public void deleteById(long id) {
        this.locationLibrary.deleteById(id);
    }
}