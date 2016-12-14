package team.code.effect.digitalbinder.photobook;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipFile;

import team.code.effect.digitalbinder.R;
import team.code.effect.digitalbinder.common.AppConstans;
import team.code.effect.digitalbinder.common.ColorPaletteHelper;
import team.code.effect.digitalbinder.main.MainActivity;

/**
 * Created by student on 2016-12-09.
 */

public class PhotobookRecyclerAdapter extends RecyclerView.Adapter<PhotobookViewHolder> {
    PhotobookActivity photobookActivity;
    ArrayList<Photobook> list = (ArrayList)MainActivity.dao.selectAll();
    boolean isDeleteMemuClicked = false;
    int [][] rgb = {
            {99, 166, 159},
            {242, 225, 172},
            {242, 131, 107}
    };

    public PhotobookRecyclerAdapter(PhotobookActivity photobookActivity) {
        this.photobookActivity = photobookActivity;
    }

    @Override
    public PhotobookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photobook, parent, false);
        return new PhotobookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotobookViewHolder holder, int position) {
        Photobook photobook = list.get(position);
        String title = photobook.getTitle();
        StringBuffer sb = new StringBuffer();
        int count = 0;
        String regdate = photobook.getRegdate().substring(0, 11);

        //포토북 제목 세로로 출력.
        for(int i=0; i<title.length(); ++i){
            sb.append(title.charAt(i));
            if(i != title.length()-1){
                sb.append("\n");
            }
        }

        //파일 개수 세기
        try {
            ZipFile zipFile = new ZipFile(AppConstans.APP_PATH+"/"+photobook.getFilename());
            count = zipFile.size();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //파일이 등록된 날짜를 보기 좋게 변환.


        holder.txt_title.setText(sb.toString());
        holder.txt_count.setText(Integer.toString(count));
        holder.txt_regdate.setText(regdate.replaceFirst("-", "\n").replaceFirst("-", "."));
        holder.layout_photobook.setBackgroundResource(ColorPaletteHelper.VALUE[photobook.getColor()]);
        holder.ib_bookmark_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ib_bookmark_false.setVisibility(View.GONE);
                holder.ib_bookmark_true.setVisibility(View.VISIBLE);
            }
        });
        holder.ib_bookmark_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ib_bookmark_false.setVisibility(View.VISIBLE);
                holder.ib_bookmark_true.setVisibility(View.GONE);
            }
        });
        if(!isDeleteMemuClicked)
            holder.checkBox.setVisibility(View.GONE);
        else
            holder.checkBox.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<Photobook> list) {
        this.list = list;
    }
}
