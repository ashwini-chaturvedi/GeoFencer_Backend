package admin.admin.Service;


import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import admin.admin.Entity.Location;

@Service
public class GoogleService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private GeoApiContext geoApiContext;

    @PostConstruct
    public void init() {
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    public String calculateDistance(Location homeLocation,Location deviceLocation) {
        try {
            if (deviceLocation == null || homeLocation==null) {
                return "0.00";
            }

            DistanceMatrix matrix = DistanceMatrixApi.newRequest(geoApiContext)
                    .origins(new com.google.maps.model.LatLng(homeLocation.getLatitude(), homeLocation.getLongitude()))
                    .destinations(new com.google.maps.model.LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude()))
                    .mode(TravelMode.DRIVING)
                    .await();

            if (matrix.rows.length > 0 && matrix.rows[0].elements.length > 0) {
                long distanceInMeters = matrix.rows[0].elements[0].distance.inMeters;
                double distanceInKm = distanceInMeters / 1000.0;
                return String.format("%.2f", distanceInKm);
            }

            return "0.00";
        } catch (Exception e) {
            e.printStackTrace();
            return "N/A";
        }
    }
}