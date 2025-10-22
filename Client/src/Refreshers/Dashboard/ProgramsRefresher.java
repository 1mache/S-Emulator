package Refreshers.Dashboard;

import newGui.pages.dashboard.component.availableFunctions.availableFunctionsController;
import newGui.pages.dashboard.component.availablePrograms.availableProgramsController;
import newGui.pages.dashboard.component.primary.dashboardController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import requests.ProgramListRequest;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Periodic refresher that fetches all programs/functions and updates the dashboard.
 */
public class ProgramsRefresher {

    private final dashboardController dashboardController;

    private final availableProgramsController programsController;
    private final availableFunctionsController functionsController;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ProgramsRefresher");
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> taskHandle;

    private final long refreshPeriodSeconds;


    public ProgramsRefresher(availableProgramsController programsController,
                             availableFunctionsController functionsController,
                             dashboardController dashboardController,
                             long refreshPeriodSeconds) {
        this.programsController = Objects.requireNonNull(programsController);
        this.functionsController = Objects.requireNonNull(functionsController);
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
        HttpClientUtil.runAsync(ProgramListRequest.build(), new Callback() {
            @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ProgramListRequest.onFailure(e);
            }
            @Override public void onResponse(@NotNull Call call, @NotNull Response response) {
                ProgramListRequest.onResponse(response, dashboardController);
            }
        });
    }

    public void shutdown() {
        stop();
        scheduler.shutdownNow();
    }
}

