package exemplo.fogo360

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import exemplo.fogo360.MainActivity
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.UUID

class MqttManager(context: Context) {
    private val brokerUrl = BuildConfig.BROKEN_URL
    private val clientId = "android_${UUID.randomUUID()}"

    // criação do cliente MQTT com persistência em memória
    private val mqttClient = MqttAndroidClient(context, brokerUrl, clientId, MemoryPersistence())
    private var TAG = "MQTT"


    init {
        // configura o cliente MQTT
        configureMqttClient()
    }

    private var messageTextView: TextView? = null

    fun setTextView(textView: TextView) {
        this.messageTextView = textView
    }

    private fun configureMqttClient() {
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val mensagemRecebida = message?.toString() ?: "Mensagem vazia"
                Log.d("MqttManager", "Mensagem recebida: $mensagemRecebida")

                messageTextView?.text = mensagemRecebida
            }

            override fun connectionLost(cause: Throwable?) {
                Log.d("MqttManager", "Conexão perdida: ${cause.toString()}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })
    }

    // função para conectar ao broker MQTT
    fun connect(context: MainActivity) {
        if (!mqttClient.isConnected) {
            val options = MqttConnectOptions()
            options.userName = BuildConfig.MQTT_USERNAME
            options.password = BuildConfig.MQTT_PASSWORD.toCharArray()

            try {
                // conecta ao broker MQTT
                mqttClient.connect(options, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        // callback chamado em caso de conexão bem-sucedida
                        Log.d(TAG, "Conexão bem-sucedida")

                        // inscreve-se em um tópico e publica uma mensagem de teste
                        subscribe("/fogo360/falls")
                        publish("/fogo360/falls", "Conexão com HiveMQ estabelecida com sucesso!")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        // callback chamado em caso de falha na conexão
                        Log.e(TAG, "Falha na conexão", exception)
                        exception?.printStackTrace()
                    }
                })
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        } else {
            // o cliente MQTT já está conectado
            val message = "O cliente MQTT já está conectado."
            val show = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "O cliente MQTT já está conectado.")
        }
    }

    // função para se inscrever em um tópico MQTT
    fun subscribe(topic: String, qos: Int = 1) {
        try {
            if (mqttClient.isConnected) {
                // se o cliente MQTT está conectado, inscreve-se no tópico
                mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        // callback chamado em caso de sucesso na inscrição
                        Log.d(TAG, "Inscrito em $topic")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        // callback chamado em caso de falha na inscrição
                        Log.d(TAG, "Falha ao se inscrever em $topic")
                    }
                })
            } else {
                // o cliente MQTT não está conectado
                Log.d(TAG, "O cliente MQTT não está conectado.")
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // função para publicar uma mensagem em um tópico MQTT
    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {
            if (mqttClient.isConnected) {
                // se o cliente MQTT está conectado, publica a mensagem no tópico
                val message = MqttMessage()
                message.payload = msg.toByteArray()
                message.qos = qos
                message.isRetained = retained

                mqttClient.publish(topic, message, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        // callback chamado em caso de sucesso na publicação
                        Log.d(TAG, "$msg publicado em $topic")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        // callback chamado em caso de falha na publicação
                        Log.d(TAG, "Falha ao publicar $msg em $topic")
                    }
                })
            } else {
                // o cliente MQTT não está conectado
                Log.d(TAG, "O cliente MQTT não está conectado.")
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // função para desconectar do broker MQTT
    fun disconnect() {
        try {
            if (mqttClient.isConnected) {
                // se o cliente MQTT está conectado, desconecta
                mqttClient.disconnect(null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        // callback chamado em caso de sucesso na desconexão
                        Log.d(TAG, "Desconectado")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        // callback chamado em caso de falha na desconexão
                        Log.d(TAG, "Falha ao desconectar")
                    }
                })
            } else {
                // o cliente MQTT não está conectado
                Log.d(TAG, "O cliente MQTT não está conectado.")
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // verifica se o cliente MQTT está conectado
    fun isConnected(): Boolean {
        return mqttClient.isConnected
    }
}






