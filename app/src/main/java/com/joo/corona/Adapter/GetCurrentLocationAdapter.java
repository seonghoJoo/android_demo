package com.joo.corona.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joo.corona.Data.GetCurrentLocation;
import com.joo.corona.R;

import java.util.ArrayList;

public class GetCurrentLocationAdapter extends RecyclerView.Adapter<GetCurrentLocationAdapter.ViewHolder> {

    Context context;
    ArrayList<GetCurrentLocation> items = new ArrayList<GetCurrentLocation>();

    public GetCurrentLocationAdapter(Context context){
        this.context = context;
    }
    //viewholder객체가 만들어질때 만들어짐
    @NonNull
    @Override
    public GetCurrentLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.buildingitem,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GetCurrentLocationAdapter.ViewHolder holder, int position) {
        GetCurrentLocation item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void addItem (GetCurrentLocation item){
        items.add(item);
    }
    public void addItems(ArrayList<GetCurrentLocation> items){
        this.items = items;
    }
    public GetCurrentLocation getItem(int position){
        return items.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView1,textView2,textView3;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = (TextView) itemView.findViewById(R.id.item_lat);
            textView2 = (TextView) itemView.findViewById(R.id.item_lng);
            textView3 = (TextView) itemView.findViewById(R.id.item_date);
        }
        public void setItem(GetCurrentLocation item){
            textView1.setText(item.getLatitude()+ "");
            textView2.setText(item.getLongitude()+ "");
            textView3.setText(item.getT()+"");
        }
    }
}
