package com.numero.simplehome.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.numero.simplehome.R;

import java.util.List;

import io.reactivex.Observable;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private PackageManager packageManager;
    private final List<ApplicationInfo> applicationInfoList;

    public AppListAdapter(@NonNull Context context, @NonNull List<ApplicationInfo> applicationInfoList) {
        this.packageManager = context.getPackageManager();
        this.applicationInfoList = applicationInfoList;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        Observable.just(applicationInfoList.get(position))
                .map(applicationInfo -> packageManager.getApplicationLabel(applicationInfo))
                .map(charSequence -> (String) charSequence)
                .subscribe(holder::setName);
    }

    @Override
    public int getItemCount() {
        return applicationInfoList.size();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;

        public AppViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name_text);
        }

        public void setName(String name) {
            nameTextView.setText(name);
        }
    }
}
