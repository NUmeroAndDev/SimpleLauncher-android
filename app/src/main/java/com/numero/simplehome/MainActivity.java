package com.numero.simplehome;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
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

        executeLoadApplication();
    }

    @Override
    public void onBackPressed() {
    }

    private void initList() {
        adapter = new AppListAdapter(appList);
        adapter.setOnItemClickListener((view, position) -> {
            App app = appList.get(position);
            launchApp(app);
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void launchApp(@NonNull App app) {
        Observable.just(app)
                .map(a -> getPackageManager().getLaunchIntentForPackage(a.getPackageName()))
                .subscribe(this::startActivity);
    }

    private void executeLoadApplication() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appInfoList = getPackageManager().queryIntentActivities(intent, 0);

        if (appInfoList == null) {
            return;
        }

        Observable.fromIterable(appInfoList)
                .map(resolveInfo -> {
                    String name = Observable.just(resolveInfo)
                            .map(info -> info.loadLabel(getPackageManager()))
                            .map(charSequence -> (String) charSequence)
                            .blockingFirst();
                    Drawable icon = Observable.just(resolveInfo)
                            .map(info -> info.loadIcon(getPackageManager()))
                            .blockingFirst();
                    return new App(name, icon, resolveInfo.activityInfo.packageName);
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
