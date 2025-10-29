package web.utils;

import com.google.gson.Gson;
import dto.ProgramIdentifier;
import dto.server.response.ProgramData;
import engine.api.SLanguageEngine;
import engine.instruction.Architecture;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import web.context.AppContext;
import web.user.User;

import java.io.IOException;
import java.util.Map;

public class ServletUtils {
    private static final String APP_CONTEXT_ATR_NAME = "appContext";
    public static final String USERNAME_ATR_NAME = "username";

    private static final Object APP_CONTEXT_LOCK = new Object();

    public static final Gson GsonInstance = new Gson();

    public static final Map<Architecture, Integer> architectureCosts = Map.ofEntries(
            Map.entry(Architecture.I, 5),
            Map.entry(Architecture.II, 100),
            Map.entry(Architecture.III, 500),
            Map.entry(Architecture.IV, 1000)
    );

    public static AppContext getAppContext(ServletContext servletContext) {
        AppContext appContext;
        synchronized (APP_CONTEXT_LOCK) {
            appContext = (AppContext) servletContext.getAttribute(APP_CONTEXT_ATR_NAME);
            if(appContext == null) {
                servletContext.setAttribute(APP_CONTEXT_ATR_NAME, new AppContext());
                appContext = (AppContext) servletContext.getAttribute(APP_CONTEXT_ATR_NAME);
            }
        }

        return appContext;
    }

    public static ProgramData buildProgramDataObject(ProgramIdentifier programIdentifier, int expansionDegree,
                                                     AppContext context) {
        String name = programIdentifier.name();
        SLanguageEngine engine = context.getEngine();
        return new ProgramData(
                name,
                programIdentifier.userString(),
                programIdentifier.isMain(),
                context.getFunctionOwner(name),
                engine.instructionCountOf(name),
                engine.getMaxExpansionDegree(name),
                engine.getRunCountOf(name),
                engine.getAverageCostOf(name),
                engine.getArchitectureOf(name, expansionDegree).name()
        );
    }

    public static boolean chargeArchitectureCost(User user, Architecture arch){
        int archCost = ServletUtils.architectureCosts.get(arch);
        if(user.getTotalCredits() < archCost)
            return false;
        user.removeCredits(archCost);
        return true;
    }
}
