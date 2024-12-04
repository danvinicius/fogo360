package exemplo.fogo360

import android.content.pm.PackageManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView


class MainActivity : AppCompatActivity(), LocationManager.LocationCallback {

    private lateinit var locationManager: LocationManager

    companion object {
        @Volatile
        internal var instance: MainActivity? = null

        @JvmStatic
        fun getInstance(): MainActivity? {
            return instance
        }

        @JvmStatic
        fun setInstance(activity: MainActivity?) {
            instance = activity
        }
    }

    // inicialização do Button usando lazy initialization
    private val enterButton: Button by lazy {
        findViewById(R.id.enterButton)
    }

    // Inicialize o MqttManager
    private val mqttManager: MqttManager by lazy {
        MqttManager(this)
    }

    private lateinit var mqttMessageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mqttMessageTextView = findViewById(R.id.mqttMessageTextView)

        // Configuração de MQTT
        conectarAoMqtt()
    }

    private fun conectarAoMqtt() {
        try {
            if (!mqttManager.isConnected()) {
                mqttManager.connect(this)
            } else {
                Log.d("MainActivity", "MQTT já conectado.")
            }

            // Receba mensagens do MQTT e exiba na tela
            mqttManager.setTextView(mqttMessageTextView)
        } catch (e: Exception) {
            Log.e("MainActivity", "Erro ao conectar ao MQTT.", e)
        }
    }

    fun processMqttConnectionMessage(mensagemRecebida: String) {
        runOnUiThread {
            mqttMessageTextView.text = mensagemRecebida
        }
    }

    override fun onLocationPermissionGranted() {
        Log.d("SecondActivity", "Location permission granted in SecondActivity")

        // Verificar se a permissão de localização está concedida
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permissão concedida, obter a última localização
            locationManager.getLastLocation()
        } else {
            // Permissão não concedida, solicitar permissão (isso deveria ser tratado anteriormente)
            Log.e("SecondActivity", "Location permission not granted.")
        }
    }

    // método chamado ao destruir a atividade
    override fun onDestroy() {
        super.onDestroy()
        // desconectar do broker MQTT ao destruir a atividade
        mqttManager.disconnect()
    }
}



