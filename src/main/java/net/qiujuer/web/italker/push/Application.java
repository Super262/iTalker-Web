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
        // 注册逻辑处理的包名
        //packages("net.qiujuer.web.italker.push.service");
        packages(AccountService.class.getPackage().getName());

        // 注册我们的全局请求拦截器
        register(AuthRequestFilter.class);

        // 注册Json解析器
        // register(JacksonJsonProvider.class);
        // 替换解析器为Gson
        register(GsonProvider.class);

        // 注册日志打印输出
        register(Logger.class);

        // 启动时直接初始化Hibernate数据库
        Hib.setup();

        // 输出启动成功日志
        LOGGER.log(Level.INFO, "Application setup succeed!");

    }
}
