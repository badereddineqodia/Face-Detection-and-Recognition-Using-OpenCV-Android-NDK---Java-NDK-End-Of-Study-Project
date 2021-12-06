package com.badereddine.qodia.smipfes6;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import com.badereddine.qodia.smipfes6.PersonRecognizer;
import com.badereddine.qodia.smipfes6.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.opencv.objdetect.CascadeClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import static com.badereddine.qodia.smipfes6.PersonRecognizer.HEIGHT;
import static com.badereddine.qodia.smipfes6.PersonRecognizer.WIDTH;
import static org.opencv.core.Core.FONT_HERSHEY_PLAIN;

//import java.io.FileNotFoundException;
//import org.opencv.contrib.FaceRecognizer;

public class FdActivity extends Activity implements CvCameraViewListener2 {


    DatabaseReference databaseReference;
    BufferedWriter bw;
    public static String desc=null;
    public static String shareName="unknown";
    public static ArrayList<String> shareNameScreen;
    private static int countframe=0;
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;
    public static final int TRAINING = 0;
    public static final int SEARCHING = 1;
    public static final int IDLE = 2;
    static final long MAXIMG = 20;
    private static final String TAG = "OCVSample::Activity";
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private static final int frontCam = 1;
    private static final int backCam = 2;
//    private int countTrain=0;
    String mPath = "";
    EditText text;
    TextView textresult;
    Bitmap mBitmap;
    Handler mHandler;
    PersonRecognizer fr;
    ToggleButton toggleButtonGrabar, toggleButtonTrain, buttonSearch;
    //   private DetectionBasedTracker  mNativeDetector;
    Button buttonCatalog,viewPersonneFromFirebase;
    ImageView ivGreen, ivYellow, ivRed;
    ImageButton imCamera;
    TextView textState;
    com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer faceRecognizer;
    ArrayList<Mat> alimgs = new ArrayList<Mat>();
    int[] labels = new int[(int) MAXIMG];
    int countImages = 0;
    labels labelsFile;
    private int faceState = IDLE;
    //    private MenuItem               mItemFace50;
//    private MenuItem               mItemFace40;
//    private MenuItem               mItemFace30;
//    private MenuItem               mItemFace20;
//    private MenuItem               mItemType;
//
    private MenuItem nBackCam;
    private MenuItem mFrontCam;
    private MenuItem mEigen;
    private Mat mRgba;
    private Mat mGray;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private int mDetectorType = JAVA_DETECTOR;
    private String[] mDetectorName;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private int mLikely = 999;
    private Tutorial3View mOpenCvCameraView;
    private int mChooseCamera = backCam;
    private ImageView Iv;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    //   System.loadLibrary("detection_based_tracker");


                    fr = new PersonRecognizer(mPath);
                    String s = getResources().getString(R.string.Straininig);
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    fr.load();

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        //                 mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;


            }
        }
    };

    public FdActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.face_detect_surface_view);

        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.tutorial3_activity_java_surface_view);

        mOpenCvCameraView.setCvCameraViewListener(this);


        mPath = getFilesDir() + "/facerecogOCV/";

        labelsFile = new labels(mPath);

        Iv = (ImageView) findViewById(R.id.imageView1);
        textresult = (TextView) findViewById(R.id.textView1);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj == "IMG") {
                    Canvas canvas = new Canvas();
                    canvas.setBitmap(mBitmap);
                    Iv.setImageBitmap(mBitmap);
                    if (countImages >= MAXIMG - 1) {
                        toggleButtonGrabar.setChecked(false);
                        grabarOnclick();
                    }
                } else {

                        if(!shareName.equals(msg.obj.toString()) && !isEqual(msg.obj.toString())){

                            try {

                                bw.write(msg.obj.toString()+"_"+mLikely);
                                bw.newLine();



                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                Log.e("error", e.getMessage() + " " + e.getCause());
                                e.printStackTrace();
                            }
                        }

                    //shareName+=msg.obj.toString()+".";
                    shareName=msg.obj.toString();
                    shareNameScreen.add(shareName);


                    System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                    textresult.setText(msg.obj.toString());
                    ivGreen.setVisibility(View.INVISIBLE);
                    ivYellow.setVisibility(View.INVISIBLE);
                    ivRed.setVisibility(View.INVISIBLE);

                    if (mLikely < 0) ;
                    else if (mLikely < 50) {
                        ivGreen.setVisibility(View.VISIBLE);
                        System.out.println("tres biennn    1");
                    }
                    else if (mLikely < 80) {
                        ivYellow.setVisibility(View.VISIBLE);
                        System.out.println("tres biennn    2");

                    }
                    else
                        ivRed.setVisibility(View.VISIBLE);
                        //shareName="unknown";
                }
            }
        };
        text = (EditText) findViewById(R.id.editText1);
        buttonCatalog = (Button) findViewById(R.id.buttonCat);
        toggleButtonGrabar = (ToggleButton) findViewById(R.id.toggleButtonGrabar);
        buttonSearch = (ToggleButton) findViewById(R.id.buttonBuscar);
        toggleButtonTrain = (ToggleButton) findViewById(R.id.toggleButton1);
        textState = (TextView) findViewById(R.id.textViewState);
        ivGreen = (ImageView) findViewById(R.id.imageView3);
        ivYellow = (ImageView) findViewById(R.id.imageView4);
        ivRed = (ImageView) findViewById(R.id.imageView2);
        imCamera = (ImageButton) findViewById(R.id.imageButton1);
        viewPersonneFromFirebase=findViewById(R.id.viewPersonneFromFirebase);

        ivGreen.setVisibility(View.INVISIBLE);
        ivYellow.setVisibility(View.INVISIBLE);
        ivRed.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
        textresult.setVisibility(View.INVISIBLE);


        toggleButtonGrabar.setVisibility(View.INVISIBLE);

        viewPersonneFromFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),PersonnesRecongnized.class);
                startActivity(intent);

            }
        });
        buttonCatalog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(com.badereddine.qodia.smipfes6.FdActivity.this,
                        com.badereddine.qodia.smipfes6.ImageGallery.class);
                i.putExtra("path", mPath);
                startActivity(i);
            }

            ;
        });


        text.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((text.getText().toString().length() > 0) && (toggleButtonTrain.isChecked()))
                    toggleButtonGrabar.setVisibility(View.VISIBLE);
                else
                    toggleButtonGrabar.setVisibility(View.INVISIBLE);

                return false;
            }
        });


        toggleButtonTrain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (toggleButtonTrain.isChecked()) {
                    textState.setText(getResources().getString(R.string.SEnter));
                    buttonSearch.setVisibility(View.INVISIBLE);
                    textresult.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                    textresult.setText(getResources().getString(R.string.SFaceName));
                    if (text.getText().toString().length() > 0)
                        toggleButtonGrabar.setVisibility(View.VISIBLE);


                    ivGreen.setVisibility(View.INVISIBLE);
                    ivYellow.setVisibility(View.INVISIBLE);
                    ivRed.setVisibility(View.INVISIBLE);


                } else {
                    textState.setText(R.string.Straininig);
                    textresult.setText("");
                    text.setVisibility(View.INVISIBLE);

                    buttonSearch.setVisibility(View.VISIBLE);
                    ;
                    textresult.setText("");
                    {
                        toggleButtonGrabar.setVisibility(View.INVISIBLE);
                        text.setVisibility(View.INVISIBLE);
                    }
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Straininig), Toast.LENGTH_LONG).show();
                    fr.train();
                    textState.setText(getResources().getString(R.string.SIdle));

                }
            }

        });


        toggleButtonGrabar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                grabarOnclick();
            }
        });

        imCamera.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (mChooseCamera == frontCam) {
                    mChooseCamera = backCam;
                    mOpenCvCameraView.setCamBack();
                } else {
                    mChooseCamera = frontCam;
                    mOpenCvCameraView.setCamFront();

                }
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (buttonSearch.isChecked()) {



                    if (!fr.canPredict()) {
                        buttonSearch.setChecked(false);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.SCanntoPredic), Toast.LENGTH_LONG).show();
                        return;
                    }

                    shareNameScreen=new ArrayList <>();

                    File f = new File(mPath + "personLibraryFS.txt");
                    try{
                        f.createNewFile();
                        bw = new BufferedWriter(new FileWriter(f));
                    }catch (IOException e){

                    }

                    textState.setText(getResources().getString(R.string.SSearching));
                    toggleButtonGrabar.setVisibility(View.INVISIBLE);
                    toggleButtonTrain.setVisibility(View.INVISIBLE);
                    text.setVisibility(View.INVISIBLE);
                    faceState = SEARCHING;
                    textresult.setVisibility(View.VISIBLE);
                } else {

                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    databaseReference= FirebaseDatabase.getInstance().getReference();
                    //databaseReference.child("personnesRecongnized").setValue();

                        File file = new File(mPath + "personLibraryFS.txt");
                    BufferedReader r;
                    Personne p;
                    String dateTime=DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
                   try{
                        r = new BufferedReader(new FileReader(file));
                        int count=0;
                       for (String line; (line = r.readLine()) != null; ) {
                           p=new Personne("personne_"+count,line.split("_")[0]+"-00000.jpg",mPath, dateTime,line.split("_")[1]);

                           databaseReference.child("personnesRecongnized").child(dateTime).child("personne_"+count++).setValue(p);

                       }
                       r.close();
                   }catch (FileNotFoundException e){

                   }catch (IOException ee){

                   }


                    System.out.println("yyyyyyyyyyyyyyyyyyyyyyyy");

                    faceState = IDLE;
                    textState.setText(getResources().getString(R.string.SIdle));
                    toggleButtonGrabar.setVisibility(View.INVISIBLE);
                    toggleButtonTrain.setVisibility(View.VISIBLE);
                    text.setVisibility(View.INVISIBLE);
                    textresult.setVisibility(View.INVISIBLE);

                }
            }
        });

        boolean success = (new File(mPath)).mkdirs();
        if (!success) {
            Log.e("Error", "Error creating directory");
        }
    }

    void grabarOnclick() {
        if (toggleButtonGrabar.isChecked())
            faceState = TRAINING;
        else {
            if (faceState == TRAINING) ;
            // train();
            //fr.train();
            countImages = 0;
            faceState = IDLE;
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public boolean isEqual(String s){

        for(int i=0;i<shareNameScreen.size();i++){
            if(shareNameScreen.get(i).equals(s)) return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);


    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();

    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();

    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            //  mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        } else if (mDetectorType == NATIVE_DETECTOR) {
//            if (mNativeDetector != null)
//                mNativeDetector.detect(mGray, faces);
        } else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();

        if ((facesArray.length == 1) && (faceState == TRAINING) && (countImages < MAXIMG) && (!text.getText().toString().isEmpty())) {


            Mat m = new Mat();
            Rect r = facesArray[0];


            m = mRgba.submat(r);
            mBitmap = Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);


            Utils.matToBitmap(m, mBitmap);
            // SaveBmp(mBitmap,"/sdcard/db/I("+countTrain+")"+countImages+".jpg");

            Message msg = new Message();
            String textTochange = "IMG";
            msg.obj = textTochange;
            mHandler.sendMessage(msg);

            if (countImages < MAXIMG) {
                fr.add(m, text.getText().toString());
                countImages++;
            }

                Bitmap bmp = Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);

                Utils.matToBitmap(m, bmp);
                bmp = Bitmap.createScaledBitmap(bmp, WIDTH, HEIGHT, false);


                FileOutputStream f;
                try {
                    f = new FileOutputStream(mPath + text.getText().toString() + "-00000.jpg", true);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, f);
                    f.close();

                } catch (Exception e) {
                    Log.e("error", e.getCause() + " " + e.getMessage());
                    e.printStackTrace();

                }


        } else if ((facesArray.length > 0) && (faceState == SEARCHING)) {
            Mat m = new Mat();
            m = mGray.submat(facesArray[0]);
            mBitmap = Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);


            Utils.matToBitmap(m, mBitmap);
            Message msg = new Message();
            String textTochange = "IMG";
            msg.obj = textTochange;
            mHandler.sendMessage(msg);



            textTochange = fr.predict(m);//recuperer le nom de personne reconnue
            mLikely = fr.getProb();//recuperer le pourcentage de ressemblance




            msg = new Message();
            msg.obj = textTochange;
            mHandler.sendMessage(msg);

        }
        for (int i = 0; i < facesArray.length; i++) {
            Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
            Point point=new Point(facesArray[i].tl().x,facesArray[i].tl().y-20);
            Core.putText(mRgba,shareName,point, FONT_HERSHEY_PLAIN,4, new Scalar(0,200,200), 4);
        }


        return mRgba;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        if (mOpenCvCameraView.numberCameras() > 1) {
            nBackCam = menu.add(getResources().getString(R.string.SFrontCamera));
            mFrontCam = menu.add(getResources().getString(R.string.SBackCamera));
//        mEigen = menu.add("EigenFaces");
//        mLBPH.setChecked(true);
        } else {
            imCamera.setVisibility(View.INVISIBLE);

        }
        //mOpenCvCameraView.setAutofocus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
//        if (item == mItemFace50)
//            setMinFaceSize(0.5f);
//        else if (item == mItemFace40)
//            setMinFaceSize(0.4f);
//        else if (item == mItemFace30)
//            setMinFaceSize(0.3f);
//        else if (item == mItemFace20)
//            setMinFaceSize(0.2f);
//        else if (item == mItemType) {
//            mDetectorType = (mDetectorType + 1) % mDetectorName.length;
//            item.setTitle(mDetectorName[mDetectorType]);
//            setDetectorType(mDetectorType);
//        
//        }
        nBackCam.setChecked(false);
        mFrontCam.setChecked(false);
        //  mEigen.setChecked(false);
        if (item == nBackCam) {
            mOpenCvCameraView.setCamFront();
            mChooseCamera = frontCam;
        }
        //fr.changeRecognizer(0);
        else if (item == mFrontCam) {
            mChooseCamera = backCam;
            mOpenCvCameraView.setCamBack();

        }

        item.setChecked(true);

        return true;
    }

    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    private void setDetectorType(int type) {
//        if (mDetectorType != type) {
//            mDetectorType = type;
//
//            if (type == NATIVE_DETECTOR) {
//                Log.i(TAG, "Detection Based Tracker enabled");
//                mNativeDetector.start();
//            } else {
//                Log.i(TAG, "Cascade detector enabled");
//                mNativeDetector.stop();
//            }
//        }
    }


}
