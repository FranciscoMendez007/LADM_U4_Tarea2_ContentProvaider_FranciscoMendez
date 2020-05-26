package mx.edu.ittepic.ladm_u4_tarea2_contentprovaider_parte1

import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val siPermisoEnviar = 1
    val siPermisoRecivido = 2
    val siPermisoLectura = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECEIVE_SMS),siPermisoRecivido)
        }
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_SMS),siPermisoLectura)
        }else{
            leerSMSEntrada()
        }
        button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS),siPermisoEnviar)
            }else{
                enviarSMS()
            }
        }
        textView2.setOnClickListener {
            try {
                val cursor = BaseDatos(this,"entrantes",null,1)
                    .writableDatabase
                    .rawQuery("SELECT * FROM ENTRANTES",null)
                var ultimo=""
                if (cursor.moveToFirst()){
                    do{
                        ultimo = "ULTIMO MENSAJE RECIBIDO\nCELULAR ORIGEN: "+ cursor.getString(0)+"\nMENSAJE SMS: "+cursor.getString(1)
                    }while (cursor.moveToNext())
                }else{
                    ultimo = "SIN MENSAJE AUN"
                }
                textView2.setText(ultimo)
            }catch (err:SQLiteException){
                Toast.makeText(this,err.message,Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == siPermisoEnviar){
            enviarSMS()
        }
        if (requestCode == siPermisoRecivido){
            mensajeRecibido()
        }
        if (requestCode == siPermisoLectura){
            leerSMSEntrada()
        }
    }

    private fun mensajeRecibido() {
        AlertDialog.Builder(this).setMessage("SE OTORGO POSIBILIDAD DE RECIBIR")
    }

    private fun enviarSMS() {
        SmsManager.getDefault().sendTextMessage(editText.text.toString(),null,editText2.text.toString(),
            null,null)
        Toast.makeText(this,"se envio el SMS", Toast.LENGTH_LONG)
            .show()
    }

    private fun leerSMSEntrada() {
        var cursor = contentResolver.query(
            Uri.parse("content://sms/"), null,null,null,null
        )
        var resultado = ""
        if (cursor!!.moveToFirst()){
            var posColumnacelularOrigen = cursor.getColumnIndex("address")
            var posColumnaMensaje = cursor.getColumnIndex("body")
            val posColumnaFecha = cursor.getColumnIndex("date")
            do {
                val fechaMensaje = cursor.getString(posColumnaFecha)
                resultado += "ORIGEN: "+cursor.getString(posColumnacelularOrigen)+"\nMENSAJE: "+cursor.getString(posColumnaMensaje)+
                        "\nFEACHA: "+Date(fechaMensaje.toLong())+"\n------------\n"

            }while (cursor.moveToNext())
        }else{
            resultado = "NO HAY MENSAJES EN LA BANDEJA"
        }
        textView3.setText(resultado)
    }
}
