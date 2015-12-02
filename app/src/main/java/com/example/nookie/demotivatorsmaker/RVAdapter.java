package com.example.nookie.demotivatorsmaker;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.nookie.demotivatorsmaker.models.RVItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
    List<RVItem> data = new ArrayList<>();

    public RVAdapter() {
        refresh();
    }

    public void refresh(){
        RefreshTask refreshTask = new RefreshTask();
        refreshTask.execute();
    }

    public void setData(List<RVItem> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)App.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.card_saved_pic,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Uri uri = data.get(position);
        holder.image.setImageBitmap(data.get(position).getThumbnail());
        holder.checkBox.setChecked(true);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.card_image)
        ImageView image;

        @Bind(R.id.card_checkbox)
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                }
            });
        }

    }

    private class RefreshTask extends AsyncTask<Void,Void,List<RVItem>>{

        @Override
        protected List<RVItem> doInBackground(Void... params) {
            FileManager fileManager = FileManager.getInstance();
            return fileManager.queryFiles();
        }

        @Override
        protected void onPostExecute(List<RVItem> rvItems) {
            setData(rvItems);
        }
    }
}
