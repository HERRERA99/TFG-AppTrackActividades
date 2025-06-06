package com.aitor.api_tfg.utils;

import com.aitor.api_tfg.model.db.LatLng;
import java.util.List;

public class GpxCalculationUtils {

    // Radio de la Tierra en kilómetros
    private static final double EARTH_RADIUS_KM = 6371;

    /**
     * Calcula la distancia total (en km) entre una lista de puntos usando la fórmula de Haversine.
     */
    public static double calculateTotalDistance(List<LatLng> points) {
        double totalDistance = 0;
        if (points == null || points.size() < 2) {
            return totalDistance;
        }

        for (int i = 1; i < points.size(); i++) {
            LatLng prevPoint = points.get(i - 1);
            LatLng currentPoint = points.get(i);
            totalDistance += calculateDistanceBetweenPoints(prevPoint, currentPoint);
        }
        return totalDistance;
    }

    /**
     * Calcula la distancia entre dos puntos (Haversine).
     */
    private static double calculateDistanceBetweenPoints(LatLng point1, LatLng point2) {
        double lat1 = Math.toRadians(point1.getLatitude());
        double lon1 = Math.toRadians(point1.getLongitude());
        double lat2 = Math.toRadians(point2.getLatitude());
        double lon2 = Math.toRadians(point2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calcula el desnivel positivo acumulado (en metros) a partir de una lista de alturas.
     */
    public static double calculateElevationGain(List<Double> elevations) {
        double elevationGain = 0;
        if (elevations == null || elevations.size() < 2) {
            return elevationGain;
        }

        for (int i = 1; i < elevations.size(); i++) {
            double diff = elevations.get(i) - elevations.get(i - 1);
            if (diff > 0) {
                elevationGain += diff;
            }
        }
        return elevationGain;
    }
}
