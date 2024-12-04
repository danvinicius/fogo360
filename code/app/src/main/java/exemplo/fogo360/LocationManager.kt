package exemplo.fogo360

import android.app.AlertDialog
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationManager(private val context: Context, private val locationCallback: MainActivity) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    interface LocationCallback {
        fun onLocationPermissionGranted()
        // Adicione outros métodos de callback conforme necessário
    }

    fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Se a permissão de localização não foi concedida, solicitar permissão
            Log.d("LocationManager", "Requesting location permission")
            ActivityCompat.requestPermissions(
                context as AppCompatActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Se a permissão já foi concedida, verificar se o serviço de localização está ativado
            checkLocationSettings()
        }
    }

    private fun checkLocationSettings() {
        // Verificar se o serviço de localização está ativado
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // O serviço de localização está desativado, exibir diálogo para ativar
            showEnableLocationDialog()
        } else {
            // O serviço de localização está ativado, chamar o callback
            Log.d("LocationManager", "Location permission already granted")
            locationCallback.onLocationPermissionGranted()
        }
    }

    private fun showEnableLocationDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Ativar Localização")
            .setMessage("A localização está desativada. Por favor, ative-a nas configurações.")
            .setPositiveButton("Configurações") { _, _ ->
                // Abrir configurações de localização
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                // Cancelar a solicitação ou fornecer feedback ao usuário
                dialog.dismiss()
            }
            .show()
    }


    fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d("Entrou", "Entrou")
                // Aqui você pode usar a localização obtida (pode ser nula)
                if (location != null) {
                    AppGlobals.minha_latitude = location.latitude
                    AppGlobals.minha_longitude = location.longitude
                    // Faça algo com a latitude e longitude
                    Log.d("LocationManager", "Latitude: $AppGlobals.latitude, Longitude: $AppGlobals.longitude")


                }else{
                    Log.e("LocationManager", "Localização não disponível")
                }
            }
            .addOnFailureListener { exception ->
                // Lidar com falhas ao obter a localização
            }
    }
}

