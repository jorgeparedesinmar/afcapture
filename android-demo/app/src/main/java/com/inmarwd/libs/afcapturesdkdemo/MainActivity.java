package com.inmarwd.libs.afcapturesdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



// Acitivity para LiveOcr
import ine.datos.com.winstondata.captureine.activities.IWD_LiveOcrIne;
// Activity para capturar fotos
import com.inmarwd.libs.afcapturesdk.activities.IWD_FotoIne;
// Activity para capturar protocolo del video
import com.inmarwd.libs.afcapturesdk.activities.IWD_Video;
// Clase para algunos elementos de captura
import com.inmarwd.libs.afcapturesdk.CaptureHelper;
// Clase que controla el Web Api del envio de datos a la nube
import com.inmarwd.libs.afcapturesdk.SendHelper;





import org.json.JSONObject;



// Implementar el Interface SendHelpler.IAFProgressEvent permite interceptar los eventos
// de envio con "OnProgress"
public class MainActivity extends AppCompatActivity implements SendHelper.IAFProgressEvent {


    // Eventos de envio de archivos
    @Override
    public void OnProgress(String action, String result, JSONObject jsonObject)
    {

        // Indica que ya tenemos un JOB generado, lo podemos guardar
        if(action.equals("create_job_ok"))
        {
            Log.wtf("GETJOB","tenemos un job");
            jobactual=result;
        }

        // Ocurrió un error al crear job, se detiene el proceso
        if(action.equals("create_job_error"))
        {
            // tomamos accion
        }
        // indica que ya se enviaron todos los archivos
        if(action.equals("files_sent_ok"))
        {
            Log.wtf("FILES_SET","ARCHIVOS ENVIADOS A PROCESAMIENTO");
            // a partir de aqui se puede revisar el estatus del job

            Toast.makeText(this,"Archivos enviados a procesar exitosamente",Toast.LENGTH_LONG).show();
        }

        // indica que ocurrió un error al enviar los archivos, se detiene el proceso
        if(action.equals("files_sent_error"))
        {
            // tomamos acción
        }


    }



    // Datos que necesitamos para mostrar el protocolo en el prompter del video
    String promotorNombre="JUAN PEREZ RIVERA";
    String promotorNAP="1598753";
    String trabajadorCelular="8188591053";
    // String que devuelve el LiveOcr
    String datosIne=null;

    String aforeActual="PROFOTURO";
    String aforeNuevo="SURA";

    // Variables donde guardaremos las rutas de
    // foto ine frente, foto ine reverso y video

    String fileIneFrente=null;
    String fileIneReverso=null;
    String fileVideo=null;


    // Datos de licencia para enviar a la nube
    // Estos serán proporcionados para otorgar el acceso
    String url="<url base>";
    String userid="<userid>";
    String token="<token>";



    // algunos widgets para el demo
    EditText txtLog;

    // Para control de job (Reintentos, etc)
    String jobactual="";



    /**
     * Toda la interacción con el SDK es a través de startActivityForResult
     * lo cual devuelve siempre un onActivityResult
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {

            // LiveOcr devuelve un string que podemos convertir a json
            if (requestCode == CaptureHelper.TOMAR_LIVEOCR) {

                // Obtenemos los datos leidos de LiveOCR
                datosIne=data.getStringExtra("datosine");

                Log.wtf("DATOS_INE", datosIne);
                String t = txtLog.getText().toString();
                t="\nJSON INE:"+datosIne+"\n"+t;

                txtLog.setText(t);

            }

            // Foto de Frente devuelve una ruta de archivo donde quedó
            // ubicada la foto
            if (requestCode == CaptureHelper.TOMAR_FOTO_FRENTE) {

                String filename=data.getStringExtra("filename");
                Log.wtf("FOTO_FRENTE", filename);

                // Obtenemos la ruta del archivo de frente
                fileIneFrente=filename;

                Toast.makeText(this, filename,Toast.LENGTH_LONG).show();
                String t = txtLog.getText().toString();
                t= "\nFRENTE:"+filename +"\n"+t;

                txtLog.setText(t);


            }

            // Foto de Reverso devuelve una ruta de archivo donde quedó
            // ubicada la foto
            if (requestCode == CaptureHelper.TOMAR_FOTO_REVERSO) {

                String filename=data.getStringExtra("filename");
                Log.wtf("FOTO_REVERSO", filename);

                // Obtenemos la ruta de la foto de reverso
                fileIneReverso=filename;

                Toast.makeText(this, filename,Toast.LENGTH_LONG).show();
                String t = txtLog.getText().toString();
                t= "\nREVERSO:"+filename+"\n"+t;

                txtLog.setText(t);

            }

            // El video devuelve una ruta del archivo donde quedo
            // ubicado el video
            if (requestCode == CaptureHelper.TOMAR_VIDEO) {

                String filename=data.getStringExtra("filename");
                Log.wtf("VIDEO", filename);

                // Obtenemos la ruta del video
                fileVideo=filename;

                Toast.makeText(this, filename,Toast.LENGTH_LONG).show();
                String t = txtLog.getText().toString();
                t= "\nVIDEO:"+filename+"\n"+t;

                txtLog.setText(t);

            }
        }else
        {
            // En caso de que el usuario cancele limpiamos las variables
            // que correspondan de acuerdo al intent

//            if (requestCode == CaptureHelper.TOMAR_LIVEOCR)
//                datosIne=null;
//
//            if (requestCode == CaptureHelper.TOMAR_FOTO_FRENTE)
//                fileIneFrente=null;
//
//            if (requestCode == CaptureHelper.TOMAR_FOTO_REVERSO)
//                fileIneReverso=null;
//
//            if (requestCode == CaptureHelper.TOMAR_VIDEO)
//                fileVideo=null;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        Button liveOcr= findViewById(R.id.btnLiveOcr);
        liveOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Llamar al activity que hace el LiveOcr
                Intent intent = new Intent(getBaseContext(),
                        IWD_LiveOcrIne.class);
                intent.putExtra("TIPO_FOTO",CaptureHelper.TOMAR_LIVEOCR);
                startActivityForResult(intent,CaptureHelper.TOMAR_LIVEOCR);

            }
        });

        Button ineFrente = findViewById(R.id.btnFotoIneFrente);
        ineFrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Llamar al activiy que hace la toma de Foto de Frente de INE
                Intent intent = new Intent(getBaseContext(),
                        IWD_FotoIne.class);
                intent.putExtra("TIPO_FOTO",CaptureHelper.TOMAR_FOTO_FRENTE);
                startActivityForResult(intent,CaptureHelper.TOMAR_FOTO_FRENTE);
            }
        });

        Button ineReverso = findViewById(R.id.btnFotoIneReverso);

        ineReverso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),
                        IWD_FotoIne.class);
                // Llamar al activity que hace la toma de Foto de Reverso de INE
                intent.putExtra("TIPO_FOTO",CaptureHelper.TOMAR_FOTO_REVERSO);
                startActivityForResult(intent,CaptureHelper.TOMAR_FOTO_REVERSO);
            }
        });

        Button videoProtocolo=findViewById(R.id.btnVideo);
        videoProtocolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(),
                        IWD_Video.class);


                // Llamar al activity que realiza la captura de video
                // con el protocolo. Primero preparamos el texto que aparecerá
                // sobrepuesto en el video para que sea leido por el trabajador o
                // promotor

                String protocolo="";

                // Generar el texto del protocolo con ayuda de la información
                // ya leida por LiveOCR
                // Nos apoyamos en CaptureHelper y los datos leidos previamente por el LiveOcr
                // (esto es opcional) podemos pasar cualquier texto que se necesite
                CaptureHelper ch=new CaptureHelper();
                if(datosIne!=null) {
                    // pasamos el string obtenido del activity LiveOcr
                    ch.setDatosIne(datosIne);

                    // Ayuda con protocolo de Traspaso
                    // ch.getDatosIne() nos devuelve un objeto gson que pasamos a la funcion
                    // del protocolo para que tome de ahi el nombre

                    // Necesitamos pasar algunos datos que no se recaban con el Sdk como
                    // el celular del trabajador, el afore actual y nuevo, el nombre de promotor
                    // y el NAP

                    protocolo = CaptureHelper.getSpeechTraspaso(ch.getDatosIne(),trabajadorCelular,
                            aforeActual, aforeNuevo,promotorNombre,promotorNAP);

                    // EJEMPLO de Ayuda con protocolo de Registro
                    //protocolo = CaptureHelper.getSpeechRegistro((ch.getDatosIne(),trabajadorCelular,aforeNuevo,promotorNombre,promotorNAP);

                    // el putextra "PROTOCOLO" puede ser cualquier cadena
                    // arriba solo utilizamos un poco de ayuda del sdk para generar
                    // el mismo
                    intent.putExtra("PROTOCOLO",protocolo);

                    startActivityForResult(intent,CaptureHelper.TOMAR_VIDEO);

                }else
                {
                    // aqui hicimos una pequeña validacion para que primero se haga el LiveOCR y luego
                    // se mande a llamar al video, esto es opcional
                    Toast.makeText(getBaseContext(),"Debe realizar primero el LiveOcr para obtener el nombre del Trabajador",
                            Toast.LENGTH_LONG).show();
                }


            }
        });




        Button btnEnviar=findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // mandamos a llamar la funcion para que envie la información
                SendHelper sh=new SendHelper(MainActivity.this, url,"INMAR",userid,token,"F0001","TRASPASO");
                // le indicamos que publique el progreso del envio sobre este mismo
                // activity que implementa el OnProgress
                sh.setProgressListener(MainActivity.this);
                sh.sendFilesAsync(MainActivity.this,datosIne,fileIneFrente,fileIneReverso,fileVideo);

            }
        });

        txtLog=findViewById(R.id.txtLog);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

