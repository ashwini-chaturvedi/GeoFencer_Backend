package admin.admin.Library;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import admin.admin.Entity.Location;
import admin.admin.Repository.LocationRepository;

@Component
public class LocationLibrary {

    @Autowired
    private LocationRepository locationRepository;

    // CREATE
    public Location save(Location detail) {
        return this.locationRepository.save(detail);
    }

    public List<Location> saveAll(List<Location> locations) {
        return this.locationRepository.saveAll(locations);
    }

    // READ
    public Location findByDeviceId(long deviceId) {
        return this.locationRepository.findByDevice_DeviceId(deviceId);
    }
    public Location findById(long deviceId) {
        return this.locationRepository.findById(deviceId).orElseThrow(()->new EntityNotFoundException("Location not found"));
    }

    public Location findHomeLocationByDeviceId(long deviceId) {
        return this.locationRepository.findHomeLocationByDeviceId(deviceId);
    }

    public Location findCurrentLocationByDeviceId(long deviceId) {
        return this.locationRepository.findCurrentLocationByDeviceId(deviceId);
    }

    public Location findHomeLocationByDeviceIdNative(long deviceId) {
        return this.locationRepository.findHomeLocationByDeviceIdNative(deviceId);
    }

    public Location findCurrentLocationByDeviceIdNative(long deviceId) {
        return this.locationRepository.findCurrentLocationByDeviceIdNative(deviceId);
    }

    // DELETE
    public void delete(Location location) {
        this.locationRepository.delete(location);
    }

    public void deleteById(long id) {
        this.locationRepository.deleteById(id);
    }
}