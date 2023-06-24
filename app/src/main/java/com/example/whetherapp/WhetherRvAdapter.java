package com.example.whetherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WhetherRvAdapter extends RecyclerView.Adapter<WhetherRvAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WhetherRvModel> whetherRvModelArrayList;

    public WhetherRvAdapter(Context context, ArrayList<WhetherRvModel> whetherRvModelArrayList) {
        this.context = context;
        this.whetherRvModelArrayList = whetherRvModelArrayList;
    }

    @NonNull
    @Override
    public WhetherRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.wether_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WhetherRvAdapter.ViewHolder holder, int position) {

        WhetherRvModel model=whetherRvModelArrayList.get(position);

        holder.temperatureTV.setText(model.getTemperature()+" c");
        Picasso.get().load("https:".concat(model.getIcon())).into(holder.conditionIV);
        holder.windTV.setText(model.getWindSpeed()+"Km/h");
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm aa");

        try {
            Date t=input.parse(model.getTime());
            holder.timeTV.setText(output.format(t));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }



    }

    @Override
    public int getItemCount() {
        return  whetherRvModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView windTV,temperatureTV,timeTV;
        private ImageView conditionIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            windTV=itemView.findViewById(R.id.idTVWindSpeed);
            temperatureTV=itemView.findViewById(R.id.idTVTemperature);
            timeTV=itemView.findViewById(R.id.idTVTime);
            conditionIV=itemView.findViewById(R.id.idIVCondition);

        }
    }
}
