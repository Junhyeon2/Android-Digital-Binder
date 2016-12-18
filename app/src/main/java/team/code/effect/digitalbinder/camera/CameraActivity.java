package team.code.effect.digitalbinder.camera;

import android.app.Dialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;

import team.code.effect.digitalbinder.R;
import team.code.effect.digitalbinder.common.AlertHelper;
import team.code.effect.digitalbinder.common.AppConstans;
import team.code.effect.digitalbinder.common.ColorPalette;
import team.code.effect.digitalbinder.common.ColorPaletteHelper;
import team.code.effect.digitalbinder.common.ColorPaletteRecyclerAdapter;
import team.code.effect.digitalbinder.common.DeviceHelper;
import team.code.effect.digitalbinder.common.ImageFile;
import team.code.effect.digitalbinder.main.MainActivity;

public class CameraActivity extends AppCompatActivity implements SensorEventListener{
    final String TAG = getClass().getName();

    //레이아웃 관련 멤버 변수 정의
    FrameLayout preview;
    ImageButton btn_open_preview, btn_close_preview, btn_save, btn_shutter, btn_back, btn_shutter_ring;
    CustomCamera customCamera;
    View popupPreview;
    PopupWindow popupWindow;
    RecyclerView recyclerview;
    PreviewRecyclerAdapter previewRecyclerAdapter;
    ArrayList<ImageFile> list = new ArrayList<>();
    FrameLayout.LayoutParams layoutParams;

    //센서 관련 멤버 변수 정의
    SensorManager sensorManager;
    Sensor accelerometer, magnetometer;
    float[] lastAccelerometer = new float[3];
    float[] lastMagnetometer = new float[3];
    boolean lastAccelerometerSet = false;
    boolean lastMagnetometerSet = false;
    float[] rotation = new float[9];
    float[] orientationData = new float[3];
    static int orientation;
    int oldOrientation;
    int newOrientation;

    //애니메이션 관련 멤버변수 정의
    ArrayList<ImageButton> btnList = new ArrayList<ImageButton>();
    RotateAnimation rotate;
    Animation anim_shutter;

    //Color Palette 관련 멤버변수 정의
    RecyclerView recycler_view_color;
    ArrayList<ColorPalette> colorPaletteList = new ArrayList<>();
    ColorPaletteRecyclerAdapter colorPaletteRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        preview = (FrameLayout)findViewById(R.id.preview);
        btn_open_preview = (ImageButton)findViewById(R.id.btn_open_preview);
        btn_close_preview = (ImageButton)findViewById(R.id.btn_close_preview);
        btn_save = (ImageButton)findViewById(R.id.btn_save);
        btn_shutter = (ImageButton)findViewById(R.id.btn_shutter);
        btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_shutter_ring = (ImageButton)findViewById(R.id.btn_shutter_ring);
        popupPreview = View.inflate(this, R.layout.popup_preview, null);

        layoutParams = (FrameLayout.LayoutParams)btn_back.getLayoutParams();

        recyclerview = (RecyclerView)popupPreview.getRootView().findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayout.HORIZONTAL);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setHasFixedSize(true);
        previewRecyclerAdapter = new PreviewRecyclerAdapter(this);
        recyclerview.setAdapter(previewRecyclerAdapter);

        Log.d(TAG, "SDK Version: "+Build.VERSION.SDK_INT);
        customCamera = new CustomCamera(this);
        preview.addView(customCamera);
        Log.d(TAG, "Previous Camera");
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //리스트 애니메이션 적용할 버튼 리스트에 추가
        btnList.add(btn_back);
        btnList.add(btn_open_preview);
        btnList.add(btn_save);

        //적용할 애니메이션
        oldOrientation = CameraActivity.orientation;
        anim_shutter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shutter);

        //Color Palette 관련 초기화
        colorPaletteRecyclerAdapter = new ColorPaletteRecyclerAdapter();
        colorPaletteRecyclerAdapter.setList(colorPaletteList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lastAccelerometerSet = false;
        lastMagnetometerSet = false;
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void openPopupPreview(){
        btn_open_preview.clearAnimation();
        btn_save.clearAnimation();
        btn_back.clearAnimation();
        btn_open_preview.setVisibility(View.GONE);
        btn_close_preview.setVisibility(View.VISIBLE);
        btn_shutter.setEnabled(false);
        btn_shutter_ring.setImageResource(R.drawable.ic_panorama_fish_eye_gray_48dp);

        Log.d(TAG, "Popup Window Size - width: "+preview.getWidth()+", height: "+preview.getHeight());
        popupWindow = new PopupWindow(popupPreview, preview.getWidth(), preview.getHeight(), false);
        popupWindow.showAtLocation(preview, Gravity.NO_GRAVITY, 0, 0);
    }

    private void closePopupPreview() {
        btn_open_preview.setVisibility(View.VISIBLE);
        btn_close_preview.setVisibility(View.GONE);
        btn_shutter.setEnabled(true);
        btn_shutter_ring.setImageResource(R.drawable.ic_panorama_fish_eye_white_48dp);
        popupWindow.dismiss();
    }

    public void btnClick(View view){
        switch (view.getId()){
            case R.id.btn_open_preview:
                openPopupPreview();
                break;
            case R.id.btn_close_preview:
                closePopupPreview();
                break;
            case R.id.btn_shutter:
                customCamera.takePicture();
                btn_shutter.startAnimation(anim_shutter);
                btn_shutter.setEnabled(false);
                break;
            case R.id.btn_save:
                btnSaveClick();
                break;
            case R.id.btn_back:
                finishActivity();
                break;
        }
    }

    public void changeButtonRoation(){
        switch (CameraActivity.orientation){
            case DeviceHelper.ORIENTATION_REVERSE_LANDSCAPE: //180
                layoutParams.gravity=Gravity.START;
                btn_back.setLayoutParams(layoutParams);
                newOrientation = DeviceHelper.ORIENTATION_REVERSE_LANDSCAPE;
                break;
            case DeviceHelper.ORIENTATION_PORTRAIT: //90
                layoutParams.gravity=Gravity.START;
                btn_back.setLayoutParams(layoutParams);
                newOrientation = DeviceHelper.ORIENTATION_PORTRAIT;
                break;
            case DeviceHelper.ORIENTATION_LANDSCAPE: //0
                layoutParams.gravity=Gravity.END;
                btn_back.setLayoutParams(layoutParams);
                newOrientation = DeviceHelper.ORIENTATION_LANDSCAPE;
                break;
            case DeviceHelper.ORIENTATION_REVERSE_PORTRAIT: //270
                layoutParams.gravity=Gravity.END;
                btn_back.setLayoutParams(layoutParams);
                newOrientation = DeviceHelper.ORIENTATION_REVERSE_PORTRAIT;
                break;
        }

        if(oldOrientation != newOrientation){
            animateRotation(oldOrientation-90, newOrientation-90);
            oldOrientation = newOrientation;
        }
    }

    public void animateRotation(int fromAngle, int toAngle){
        if(popupWindow != null)
            if (popupWindow.isShowing())
                return;

        for(int i=0; i<btnList.size(); ++i){
            if(btnList.get(i).getId() == R.id.btn_back && toAngle == -90)
                rotate = new RotateAnimation(fromAngle, -toAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            else
                rotate = new RotateAnimation(fromAngle, toAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            btnList.get(i).startAnimation(rotate);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == accelerometer) {
            System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);
            lastAccelerometerSet = true;
        } else if (sensorEvent.sensor == magnetometer) {
            System.arraycopy(sensorEvent.values, 0, lastMagnetometer, 0, sensorEvent.values.length);
            lastMagnetometerSet = true;
        }
        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotation, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotation, orientationData);
            float pitch = (float)Math.toDegrees(orientationData[1]);
            float roll = (float) Math.toDegrees(orientationData[2]);

            if (pitch >= -45 && pitch < 45 && roll >= 45)
                orientation = DeviceHelper.ORIENTATION_REVERSE_LANDSCAPE;
            else if (pitch < -45 && roll >= -45 && roll < 45)
                orientation = DeviceHelper.ORIENTATION_PORTRAIT;
            else if (pitch >= -45 && pitch < 45 && roll < -45)
                orientation = DeviceHelper.ORIENTATION_LANDSCAPE;
            else if (pitch >= 45 && roll >= -45 && roll < 45 )
                orientation = DeviceHelper.ORIENTATION_REVERSE_PORTRAIT;
            changeButtonRoation();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void finishActivity(){
        AlertDialog.Builder alert = AlertHelper.getAlertDialog(this, "알림", "지금까지 촬영한 모든 사진이 삭제됩니다. 계속 하시겠습니까?");
        alert.setPositiveButton("뒤로가기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                list.removeAll(list);
                finish();
            }
        });
        alert.setNegativeButton("취소", null);

        alert.show();
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                closePopupPreview();
                return;
            }
        }
        finishActivity();
    }

    public void takePicture(byte[] bytes){
        //filename, orientation, takendate;
        Uri fileName = Uri.parse(AppConstans.APP_PATH_TEMP+(Integer.toString(list.size()+1))+AppConstans.EXT_IMAGE);
        String takenDate = Long.toString(System.currentTimeMillis());
        ImageFile imageFile =  new ImageFile(fileName, CameraActivity.orientation, takenDate);
        list.add(imageFile);
        new StoreTempFileAsync(this).execute(bytes);
    }

    /*
     * 파일 저장 순서
     * 1. 입력 받은 값(txt_file_name)이 중복되는지 확인한다.
     * 1-1. 중복이 된다면. 다이알로그를 닫지 않고 중복되어서 다시 압력하라고 해야함.
     * 1-2. 중복된 것이 없다면, 바이트로 저장된 이미지를 순서대로 저장한다.
     * 2. 파일 저장을 byte 파일을 바로 zip 파일로 저장할 수 있는지 찾아본다.
     * 3. 바로 저장할 수 없다면, 임시 폴더에 저장한 후 파일을 zip 파일로 압축시킨다.
     */
    public void btnSaveClick(){
        if(list.size() == 0 ) {
            Toast.makeText(this, "촬영된 사진이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = AlertHelper.getAlertDialog(this, "알림", "지금까지 촬영한 모든 사진을 하나로 묶습니다.");
        builder.setView(R.layout.layout_alert_txt);
        builder.setPositiveButton("저장", null);
        builder.setNegativeButton("취소", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                initColorPaletteList();
                recycler_view_color = (RecyclerView)((Dialog)dialogInterface).findViewById(R.id.recycler_view_color);
                GridLayoutManager layoutManager = new GridLayoutManager(((Dialog)dialogInterface).getContext(), 5);
                recycler_view_color.setLayoutManager(layoutManager);
                recycler_view_color.setAdapter(colorPaletteRecyclerAdapter);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = (Dialog)dialogInterface;
                        EditText txt_file_name = (EditText)dialog.findViewById(R.id.txt_file_name);
                        TextView txt_color = (TextView)dialog.findViewById(R.id.txt_color);

                        int colorValue;
                        //유효성 체크가 되면 AsyncTask 이용해 파일로 저장.
                        if((colorValue=checkValidity(txt_file_name, txt_color)) != -1){
                            StoreFileAsync async = new StoreFileAsync(getApplicationContext(), dialog);
                            async.execute(txt_file_name.getText().toString(), Integer.toString(colorValue));
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    public void initColorPaletteList() {
        if (colorPaletteList.size() == 0) {
            ColorPalette colorPalette;
            for (int i = 0; i < ColorPaletteHelper.VALUE.length; ++i) {
                colorPalette = new ColorPalette();
                colorPalette.setCheck(false);
                colorPalette.setColorValue(ColorPaletteHelper.VALUE[i]);
                colorPaletteList.add(colorPalette);
            }
        } else {
            for (int i = 0; i < ColorPaletteHelper.VALUE.length; ++i) {
                colorPaletteList.get(i).setCheck(false);
            }
        }
    }

    //파일명 중복 유효성 체크
    public boolean isExistFile(String filename){
        return MainActivity.dao.isDuplicatedTitle(filename);
    }
    //Color Palette 유효성 체크
    public int isCheckedColor(){
        int result = -1;
        for(int i=0; i<colorPaletteList.size(); ++i){
            if(colorPaletteList.get(i).isCheck()){
                result = i;
                break;
            }
        }
        return result;
    }

    //유효성 체크
    public int checkValidity(EditText txt_file_name, TextView txt_color){
        int result = isCheckedColor();
        boolean flagDuplicate, flagFileName, flagColor;
        //파일명 중복 여부 확인
        if(isExistFile(txt_file_name.getText()+".zip")){
            txt_file_name.setText("");
            txt_file_name.setHint("중복된 이름이 존재합니다.");
            txt_file_name.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            flagDuplicate = false;
        }else{
            flagDuplicate = true;
        }

        if(txt_file_name.getText().length() == 0){
            txt_file_name.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            flagFileName = false;
        }else{
            flagFileName = true;
        }

        //색상 선택여부를 확인
        if(result == -1) {
            txt_color.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            flagColor = false;
        }else {
            txt_color.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
            flagColor = true;
        }

        if(flagDuplicate && flagFileName && flagColor)
            return result;
        else
            return -1;
    }
}