package Refreshers.Dashboard;

import newGui.pages.dashboard.component.primary.dashboardController;
import newGui.pages.dashboard.component.usersInfo.usersTableInfoController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import requests.UsersInfoListRequest;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Periodic refresher that fetches all Users and updates the dashboard.
 */
public class UsersRefresher {

    private final dashboardController dashboardController;

    private final usersTableInfoController usersTableInfoController;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ProgramsRefresher(Uers)");
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> taskHandle;

    private final long refreshPeriodSeconds;


    public UsersRefresher(usersTableInfoController usersTableInfoController,
                             dashboardController dashboardController,
                             long refreshPeriodSeconds) {
        this.usersTableInfoController = Objects.requireNonNull(usersTableInfoController);
        this.dashboardController = Objects.requireNonNull(dashboardController);
        this.refreshPeriodSeconds = refreshPeriodSeconds <= 0 ? 10 : refreshPeriodSeconds;
    }

    public void start() {
        if (taskHandle != null && !taskHandle.isCancelled()) return;
        taskHandle = scheduler.scheduleWithFixedDelay(this::refreshOnceInternal, 0, refreshPeriodSeconds, TimeUnit.SECONDS);
    }

    public void stop() {
        if (taskHandle != null) {
            taskHandle.cancel(true);
            taskHandle = null;
        }
    }

    public void refreshOnce() {
        scheduler.execute(this::refreshOnceInternal);
    }

    private void refreshOnceInternal() {
        HttpClientUtil.runAsync(UsersInfoListRequest.build(), new Callback() {
            @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {
                UsersInfoListRequest.onFailure(e);
            }
            @Override public void onResponse(@NotNull Call call, @NotNull Response response) {
                UsersInfoListRequest.onResponse(response, dashboardController);
            }
        });
    }

    public void shutdown() {
        stop();
        scheduler.shutdownNow();
    }
}

