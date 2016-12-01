package team.code.effect.digitalbinder.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import team.code.effect.digitalbinder.R;

/**
 * Created by 1238TX on 2016-11-30.
 */

public class InfoItem extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_info);

        toolbar = (Toolbar)findViewById(R.id.toolbar); //activity_bluetooth의 id 값이 toolbar인 툴바를 연결.
        setSupportActionBar(toolbar); //toolbar를 이 화면의 앱바로 설정.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //앱바에 뒤로가기 버튼 추가.

    }

    public void bt1(View view){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:DigitalBinder@google.com"));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Android.R.id.home 앱바의 뒤로가기 버튼을 눌렀을 때.
                onBackPressed(); //액티비티가 이전 액티비티로 전환됨.
                break;
        }
        return true;
    }
}