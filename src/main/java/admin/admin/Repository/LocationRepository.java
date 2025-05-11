package admin.admin.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import admin.admin.Entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, CrudRepository<Location, Long> {
    // Two options for finding a location by device ID:

    // Option 1: Find locations where device_id column equals deviceId
    Location findByDevice_DeviceId(long deviceId);

    // Option 2: Find home locations from Device table
    @Query("SELECT d.homeLocation FROM Device d WHERE d.deviceId = :deviceId")
    Location findHomeLocationByDeviceId(@Param("deviceId") long deviceId);

    // Option 3: Find current locations from Device table
    @Query("SELECT d.currentLocation FROM Device d WHERE d.deviceId = :deviceId")
    Location findCurrentLocationByDeviceId(@Param("deviceId") long deviceId);

    // Option 4: Find by ID specifically for home locations (from Device table)
    @Query(value = "SELECT l.* FROM location l JOIN device d ON l.id = d.home_location_id WHERE d.device_id = :deviceId",
            nativeQuery = true)
    Location findHomeLocationByDeviceIdNative(@Param("deviceId") long deviceId);

    // Option 5: Find by ID specifically for current locations (from Device table)
    @Query(value = "SELECT l.* FROM location l JOIN device d ON l.id = d.current_location_id WHERE d.device_id = :deviceId",
            nativeQuery = true)
    Location findCurrentLocationByDeviceIdNative(@Param("deviceId") long deviceId);
}