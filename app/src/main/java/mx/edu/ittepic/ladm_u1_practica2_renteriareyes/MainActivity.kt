package mx.edu.ittepic.ladm_u1_practica2_renteriareyes

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        guardar.setOnClickListener {
            if(radioInterna.isChecked){
                guardarInterna()
            }
            if(radioExterna.isChecked){
                guardarExterna()
            }
        }
        abrir.setOnClickListener {
            if(radioInterna.isChecked){
                leerMemoriaInterna()
            }
            if(radioExterna.isChecked){
                leerMemoriaExterna()
            }
        }

    }

    //--------------------------------------------- ABRIR ------------------------------//

    private fun leerMemoriaInterna() {
        try{

            var flujoEntrada= BufferedReader(InputStreamReader(openFileInput(nombreArchivo.text.toString()+".txt")))
            var data=flujoEntrada.readLine()

            frase.setText(data)

        }catch (error:IOException){
            mensaje(error.message.toString())
        }
    }

    private fun leerMemoriaExterna() {
        if(noSD()){
            mensaje("NO HAY UNA MEMORIA SD")
            return
        }
        try {
            var rutaSD = Environment.getExternalStorageDirectory()
            var datosArchivo = File(rutaSD.absolutePath,nombreArchivo.text.toString()+".txt") //file (ruta,nombre)
            var flujoEntrada = BufferedReader(InputStreamReader(FileInputStream(datosArchivo))) //BufferedReader = leer por linea -- esto es acceso a la memoria interna

            var data = flujoEntrada.readLine()
            frase.setText(data)

        }catch (error : IOException){
            mensaje(error.message.toString())
        }

    }

    //--------------------------------------------- GUARDAR --------------------------//
    private fun guardarExterna() {
    //recuperar el nombre del archivo
        if (noSD()){
            mensaje("NO HAY UNA MEMORIA SD")
            return
        }
        try {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                //PERMISO NO CONCEDIDO, ENTONCES SOLICITAR EL PERMISO
                ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE),0)  //solicita el permiso, REQUESTCODE = 0 PARA DAR POR ENTENDIDO QUE SI SE PUDO REALIZAR LA ACCION
            }
            var rutaSD = Environment.getExternalStorageDirectory()
            var datosArchivo = File(rutaSD.absolutePath,nombreArchivo.text.toString()+".txt") //file (ruta,nombre)
            var flujoSalida = OutputStreamWriter( FileOutputStream(datosArchivo))
            var data = frase.text.toString()

            flujoSalida.write(data)
            flujoSalida.flush() //forzar escritura
            flujoSalida.close()
            mensaje("El archivo se ha creado correctamente en la memoria SD")
            frase.setText("")
            nombreArchivo.setText("")
        }catch (error: IOException){
            mensaje(error.message.toString())
        }
    }

    private fun guardarInterna() {
        try{ var flujoSalida = OutputStreamWriter(
            openFileOutput(nombreArchivo.text.toString()+".txt", Context.MODE_PRIVATE)
        )
            var data=frase.text.toString()
            flujoSalida.write(data) //escribir
            flujoSalida.flush() //
            flujoSalida.close() //cerrar
            mensaje("Archivo creado correctamente")
            nombreArchivo.setText("")
            frase.setText("")
        }catch (error:IOException){
            mensaje(error.message.toString())
        }
    }

    //--------------------------------------------- METHOD UTILIDADES --------------------------//

    fun noSD():Boolean{
        var estado = Environment.getExternalStorageState() //regresa el estado de si hay una SD Montada
        if(estado != Environment.MEDIA_MOUNTED) //regresa si esta montada una SD
        {
            return true
        }
        return false
    }

    private fun mensaje(s:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->}
            .show()
    }

}
