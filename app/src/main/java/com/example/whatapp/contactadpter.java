package com.example.whatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class contactadpter extends  RecyclerView.Adapter<contactadpter.MyViewHolder> {

    private Context mcontext;
    private List<profileinfo> profilelist;
    public RecyclerTouchListener.ClickListener  onhi;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,status;
        public Button button;
      public   CircleImageView circleImageView;
       public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
             status = (TextView) view.findViewById(R.id.status);
            circleImageView=(CircleImageView )view.findViewById(R.id.profile_image1);

           itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onhi.onClick(v, getAdapterPosition());
                    //  Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }

            });

        }
    }


    public contactadpter(Context context, List<profileinfo> profilelist , RecyclerTouchListener.ClickListener  onhi1) {
        this.profilelist = profilelist ;
        this.mcontext=context;
        this.onhi=onhi1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_contactadpter, parent, false);

        return new MyViewHolder(itemView);
    }




    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        profileinfo po =profilelist .get(position);
        if(!po.getName().isEmpty())
                  holder.name.setText(po.getName());
        else{
            holder.name.setText(po.getPhone());
        }
    Picasso.get().load(po.getImage()).placeholder(R.drawable.profile).into(holder.circleImageView);
       holder.status.setText(po.getStatus());
    //   holder.itemView.setOnClickListener(new View.OnClickListener() {
         ///   @Override
        //    public void onClick(View v) {
        //       Intent intent = new Intent(v.getContext(), chatprofile.class);
       ///         intent.putExtra("uid", po.getUid());
       //        intent.putExtra("name", po.getName());
         //     intent.putExtra("image", po.getImage());
        ///        mcontext.startActivity(intent);
                 // Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
      //      }

       // });

      // String s=po.getBloodgroup()+" blood are needed "+po.getBag()+" "+po.getType()+" patient in "+po.getPlace()+" phone number "+po.getPhone();
      // holder.all.setText(s);

    }

    @Override
    public int getItemCount() {
        return profilelist.size();
    }
}