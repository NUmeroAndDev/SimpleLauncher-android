package com.numero.simplehome;

import android.app.WallpaperManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private List<ApplicationInfo> applicationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        findViewById(R.id.layout).setBackground(wallpaperManager.getDrawable());
    }

    @Override
    protected void onResume() {
        super.onResume();
        executeLoadApplication();
    }

    private void executeLoadApplication() {
        Observable.create((ObservableOnSubscribe<List<ApplicationInfo>>) e -> e.onNext(getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    applicationList.addAll(list);
                }, throwable -> {
                });
    }
}
