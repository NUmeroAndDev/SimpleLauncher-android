package com.numero.simplehome;

import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import com.numero.simplehome.model.App;
import com.numero.simplehome.view.AppListAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private List<App> appList = new ArrayList<>();
    private AppListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        findViewById(R.id.layout).setBackground(wallpaperManager.getDrawable());

        initList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        executeLoadApplication();
    }

    private void initList() {
        adapter = new AppListAdapter(appList);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void executeLoadApplication() {
        Observable.fromIterable(getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA))
                .filter(applicationInfo -> getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName) != null)
                .map(applicationInfo -> {
                    String name = Observable.just(applicationInfo)
                            .map(info -> getPackageManager().getApplicationLabel(info))
                            .map(charSequence -> (String) charSequence)
                            .blockingFirst();
                    Drawable icon = Observable.just(applicationInfo)
                            .map(info -> getPackageManager().getApplicationIcon(applicationInfo))
                            .blockingFirst();
                    return new App(name, icon, applicationInfo.packageName);
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    appList.clear();
                    appList.addAll(list);
                    adapter.notifyDataSetChanged();
                }, Throwable::printStackTrace);
    }
}
