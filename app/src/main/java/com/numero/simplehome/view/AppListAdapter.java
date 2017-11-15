package com.numero.simplehome.view;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.numero.simplehome.R;
import com.numero.simplehome.model.App;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private final List<App> appList;

    public AppListAdapter(@NonNull List<App> appList) {
        this.appList = appList;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        App app = appList.get(position);
        holder.setName(app.getName());
        holder.setIcon(app.getIcon());
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private ImageView iconImageView;

        public AppViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name_text);
            iconImageView = itemView.findViewById(R.id.icon_image_view);
        }

        public void setName(String name) {
            nameTextView.setText(name);
        }

        public void setIcon(Drawable drawable) {
            iconImageView.setImageDrawable(drawable);
        }
    }
}
