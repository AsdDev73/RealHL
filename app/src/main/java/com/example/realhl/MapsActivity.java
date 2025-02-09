package com.example.realhl;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Button btnToggleMapType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtenemos el fragmento del mapa y lo configuramos
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configuramos el botón para cambiar el tipo de mapa
        btnToggleMapType = findViewById(R.id.btnToggleMapType);
        btnToggleMapType.setOnClickListener(v -> {
            if (mMap != null) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Verificar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Obtenemos la ubicación actual y añadimos el marcador correspondiente
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                // Ubicación actual del dispositivo
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(myLocation).title("Mi ubicación"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

                // Definimos dos ubicaciones recomendadas (modifica las coordenadas según lo necesites)
                LatLng recommendedA = new LatLng(36.707344711157184, -4.46730092713356); // Sala Divas
                LatLng recommendedB = new LatLng(36.691249484680746, -4.467636785418117);  // New Escnadalo,

                // Añadimos los marcadores para las ubicaciones recomendadas
                mMap.addMarker(new MarkerOptions().position(recommendedA).title("Sala Divas"));
                mMap.addMarker(new MarkerOptions().position(recommendedB).title("New Escnadalo"));

                // Calculamos la distancia entre la ubicación actual y cada recomendación
                float[] resultA = new float[1];
                Location.distanceBetween(myLocation.latitude, myLocation.longitude,
                        recommendedA.latitude, recommendedA.longitude, resultA);

                float[] resultB = new float[1];
                Location.distanceBetween(myLocation.latitude, myLocation.longitude,
                        recommendedB.latitude, recommendedB.longitude, resultB);

                // Comparamos y mostramos cuál recomendación está más cerca
                if (resultA[0] < resultB[0]) {
                    Toast.makeText(MapsActivity.this,
                            "Recomendamos la Ubicación A (más cercana)",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MapsActivity.this,
                            "Recomendamos la Ubicación B (más cercana)",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // Permite agregar un marcador al hacer pulsación larga en el mapa
        mMap.setOnMapLongClickListener(latLng -> {
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Nuevo marcador")
                    .snippet("Lat: " + latLng.latitude + ", Lng: " + latLng.longitude));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        });
    }
}
