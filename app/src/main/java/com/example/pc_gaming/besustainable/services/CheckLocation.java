package com.example.pc_gaming.besustainable.services;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckLocation extends Thread implements LocationListener {

    private LocationManager locationManager;
    private Criteria criteria;
    private String supplier;
    Location userLocation;
    Context context;


    public CheckLocation(Context context) {
        this.context = context;

        criteria = new Criteria();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        supplier = String.valueOf(locationManager.getBestProvider(criteria, true));

    }

    public void run() {


        while (!interrupted()) {
            userLocation = getLastKnownLocation();
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("User Latitud " + userLocation.getLatitude());
            System.out.println("User Longitud " + userLocation.getLongitude());
            System.out.println("User Altitude " + userLocation.getAltitude());
            System.out.println("User Accuracy " + userLocation.getAccuracy());
            System.out.println("User Provider " + userLocation.getProvider());
            System.out.println("User Speed " + userLocation.getSpeed());
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

            /**
             * Creo objeto json para poder enviarlo al servidor con el formato correcto.
             *
             * Debe tener esta estructura.
             *      deviceid: CÃ³difo Firebase para enviar notificaciÃ³n.
             *      location: {"type":"Point","coordinates":[longitud,latitud]}
             *
             *
             *      {"deviceid":"FirebaseInstanceId.getInstance().getToken()",
             *      "location":{"type":"Point","coordinates":[-5.536009478520926,50.12250890459046]}
             *      }
             */
            try {
                double[] lonlat = {userLocation.getLongitude(), userLocation.getLatitude()};
                JSONArray coordinates = new JSONArray(lonlat);
                Map location = new HashMap();
                location.put("type", "Point");
                location.put("coordinates", coordinates);
                Map userLocation = new HashMap();
                // userLocation.put("deviceid", FirebaseInstanceId.getInstance().getToken());
                userLocation.put("location", new JSONObject(location));
                userLocation.put("deviceid", "prueba");


                System.out.println(new JSONObject(userLocation));

                /**
                 *  bucle que calcula localización y la enviará¡ a servidor Node
                 *  Programada cada 10 segundo e imprime por consola
                 * Pendiente programar peticion con volley
                 * Pendiente programar lado de servidor.
                 *
                 * */
                sleep(10000);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    //Con este metodo nos aseguramos que conseguimos la localizaciÃ³n siempre.
    private Location getLastKnownLocation() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
