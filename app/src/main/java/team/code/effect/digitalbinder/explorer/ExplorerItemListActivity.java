package team.code.effect.digitalbinder.explorer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import team.code.effect.digitalbinder.R;
import team.code.effect.digitalbinder.common.BitmapHelper;
import team.code.effect.digitalbinder.common.DeviceHelper;

/**
 * Created by student on 2016-11-28.
 */

public class ExplorerItemListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    String TAG;
    Explorer explorer;
    Intent intent;

    GridLayout gridView;
    ExplorerItemAdapter explorerItemAdapter;

    RecyclerView recyclerView;
    ImageRecyclerAdapter imageRecyclerAdapter;
    Bitmap[] arrayBitmap;
    boolean isScrolled = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exploreritemlist);
        TAG=this.getClass().getName();

        Toolbar toolbar = (Toolbar) findViewById(R.id.ex_toolbar);
        setSupportActionBar(toolbar);

//        gridView=(GridLayout) findViewById(R.id.ex_grid_view);
//        explorerItemAdapter=new ExplorerItemAdapter();

        recyclerView=(RecyclerView)findViewById(R.id.recycler_thumbnail);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getApplicationContext(), 4);
        imageRecyclerAdapter=new ImageRecyclerAdapter(this);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(imageRecyclerAdapter);
        recyclerView.setItemViewCacheSize(100);
        ImageViewItemDecoration imageViewItemDecoration = new ImageViewItemDecoration(1);
        recyclerView.addItemDecoration(imageViewItemDecoration);

        intent = getIntent();
        String path = intent.getStringExtra("data");

        Log.d(TAG, path);
        File dir = new File(path);
        File[] images = dir.listFiles();
        ArrayList<String> photoPath = new ArrayList<>();


//        imageRecyclerAdapter.list.removeAll(imageRecyclerAdapter.list);


        for(int i=0;i<images.length;++i){
            Log.d(TAG, "images 실경로 "+images[i].getAbsoluteFile() );
            String filename = images[i].getAbsolutePath();
            String ext = filename.substring(filename.lastIndexOf(".")+1, filename.length());
            if(ext.equals("jpg") || ext.equals("png") || ext.equals("JPG") || ext.equals("PNG")) {
                imageRecyclerAdapter.list.add(filename);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_explorer_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
