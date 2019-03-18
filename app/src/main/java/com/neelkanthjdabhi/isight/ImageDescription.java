package com.neelkanthjdabhi.isight;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.SurfaceHolder;
import android.speech.tts.TextToSpeech;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import edmt.dev.edmtdevcognitivevision.Contract.AnalysisResult;
import edmt.dev.edmtdevcognitivevision.Contract.Caption;
import edmt.dev.edmtdevcognitivevision.Rest.VisionServiceException;
import edmt.dev.edmtdevcognitivevision.VisionServiceClient;
import edmt.dev.edmtdevcognitivevision.VisionServiceRestClient;

public class ImageDescription extends AppCompatActivity implements SurfaceHolder.Callback {

    ImageView imageView;
    TextView textView;
    GLSurfaceView surfaceView;
    Camera camera;
    private Vibrator myVib;
    private boolean vibrate = true;
    Camera.PictureCallback pictureCallback;
    SurfaceHolder surfaceHolder;
    Bitmap bitmapApi;
    TTSManager ttsManager = null;
    private final String API_KEY_ONE = "a97d0bd20a2a4922a4d4c364726139fe";
    private final String API_KEY_TWO = "7b8d1b83ef034f2e855937a674dc517b";
    private final String API_ENDPOINT_ONE = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0";
    private final String API_ENDPOINT_TWO = "https://westcentralus.api.cognitive.microsoft.com/vision/v2.0";

    VisionServiceClient visionServiceClient = new VisionServiceRestClient(API_KEY_ONE,API_ENDPOINT_ONE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_image_description);

        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.text);
        surfaceView = findViewById(R.id.suraface_view);
        surfaceView.setRenderer(new MyGLRenderer());
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(surfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        ttsManager = new TTSManager();
        ttsManager.init(this);

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        viewGroup.setOnTouchListener(new OnSwipeTouchListener(ImageDescription.this) {
            public void onSwipeTop() {
                myVib.vibrate(50);
                camera.takePicture(null,null,pictureCallback);
            }
            public void onSwipeRight() {
                myVib.vibrate(50);
                ttsManager.initQueue("You are currently on image description activity");
            }
            public void onSwipeLeft() { }
            public void onSwipeBottom() {
                myVib.vibrate(50);
                ttsManager.initQueue("Home");
                Intent intent = new Intent(ImageDescription.this,MainActivity.class);
                startActivity(intent);
            }

        });





        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                Bitmap finalBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),null,true);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(finalBitmap, bitmap.getWidth(), bitmap.getHeight(), true);

                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                bitmapApi = rotatedBitmap;

                //Process Img
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmapApi.compress(Bitmap.CompressFormat.JPEG,70,outputStream);
                final ByteArrayInputStream inputStream =  new ByteArrayInputStream(outputStream.toByteArray());
                //Api req

                AsyncTask<InputStream,String,String>  visionTask = new AsyncTask<InputStream, String, String>() {

                    ProgressDialog progressDialog = new ProgressDialog(ImageDescription.this);

                    @Override
                    protected void onPreExecute() {
                        progressDialog.show();
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }



                    @Override
                    protected void onProgressUpdate(String... values) {
                        progressDialog.setMessage(values[0]);
                    }

                    @Override
                    protected String doInBackground(InputStream... inputStreams) {
                        try
                        {
                            publishProgress("Processing");
                            ttsManager.initQueue("processing");
                            String[] features = {"Description"};
                            String[] details = {};

                            AnalysisResult result = visionServiceClient.analyzeImage(inputStreams[0],features,details);
                            String jsonResult = new Gson().toJson(result);

                            return jsonResult;

                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        } catch (VisionServiceException e)
                        {
                            e.printStackTrace();
                        }

                        return "";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        if(TextUtils.isEmpty(s))
                        {
                            Toast.makeText(ImageDescription.this,"API RETURNS NULL",Toast.LENGTH_SHORT).show();
                            //ttsManager.initQueue("No Result Found");
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }else {
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            AnalysisResult result = new Gson().fromJson(s,AnalysisResult.class);
                            StringBuilder resultString = new StringBuilder();

                            for (Caption caption : result.description.captions)
                                resultString.append(caption.text);

                            textView.setText(resultString.toString());
                            speakOut();

                        }

                    }
                };

                visionTask.execute(inputStream);
                camera.startPreview();


            }
        };


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

            try {
              camera = Camera.open();
            }catch (Exception e)
            {
                Toast.makeText(this,"11",Toast.LENGTH_SHORT).show();
            }
            Camera.Parameters parameters;
            parameters = camera.getParameters();
            parameters.setPreviewFrameRate(20);
            Camera.Size size1 = getOptimalSize(parameters,size.x,size.y);
            parameters.setPreviewSize(size1.width,size1.height);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setParameters(parameters);
            camera.setDisplayOrientation(90);

            try
            {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (Exception e)
            {
                Toast.makeText(this,"HERE",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        camera.stopPreview();
        camera.release();
        camera = null;
    }
    private void speakOut() {
        String text = textView.getText().toString();
        ttsManager.initQueue(text);
    }

    private Camera.Size getOptimalSize(Camera.Parameters params, int w, int h) {

        final double ASPECT_TH = .2; // Threshold between camera supported ratio and screen ratio.
        double minDiff = Double.MAX_VALUE; //  Threshold of difference between screen height and camera supported height.
        double targetRatio = 0;
        int targetHeight = h;
        double ratio;
        Camera.Size optimalSize = null;

        // check if the orientation is portrait or landscape to change aspect ratio.
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            targetRatio = (double) h / (double) w;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            targetRatio = (double) w / (double) h;
        }

        // loop through all supported preview sizes to set best display ratio.
        for (Camera.Size s : params.getSupportedPreviewSizes()) {

            ratio = (double) s.width / (double) s.height;
            if (Math.abs(ratio - targetRatio) <= ASPECT_TH) {

                if (Math.abs(targetHeight - s.height) < minDiff) {
                    optimalSize = s;
                    minDiff = Math.abs(targetHeight - s.height);
                }
            }
        }
        return optimalSize;
    }

}
