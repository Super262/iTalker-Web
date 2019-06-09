package net.qiujuer.web.italker.push;

import net.qiujuer.web.italker.push.provider.AuthRequestFilter;
import net.qiujuer.web.italker.push.provider.GsonProvider;
import net.qiujuer.web.italker.push.service.AccountService;
import net.qiujuer.web.italker.push.utils.Hib;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Application extends ResourceConfig {
    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());
    public Application() {

        packages(AccountService.class.getPackage().getName());

        register(AuthRequestFilter.class);

        register(GsonProvider.class);

        register(Logger.class);

        Hib.setup();

        LOGGER.log(Level.INFO, "Application setup succeed!");

    }
}
