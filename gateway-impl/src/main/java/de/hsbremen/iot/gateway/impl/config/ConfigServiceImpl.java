package de.hsbremen.iot.gateway.impl.config;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.config.Config;
import de.hsbremen.iot.gateway.api.config.GatewayConfig;
import de.hsbremen.iot.gateway.api.config.InternalConfigService;
import de.hsbremen.iot.gateway.api.exception.ConfigCheckException;
import de.hsbremen.iot.gateway.api.exception.ConfigException;
import de.hsbremen.iot.gateway.api.exception.ServiceStartupException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.representer.Representer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ConfigServiceImpl implements InternalConfigService {

    private static final String CONFIG_PATH = System.getProperty("user.home") +
            System.getProperty("file.separator") + ".gateway" +
            System.getProperty("file.separator") + "config.yaml";

    private static final Logger logger = LogManager.getLogger();

    private static final String ID = "ConfigService";


    private final String path;

    private GatewayConfig config;

    private String configAsString;

    private final Representer representer;

    private final InternalServiceRegistry serviceRegistry;

    public ConfigServiceImpl(InternalServiceRegistry serviceRegistry) {
        this.path = CONFIG_PATH;
        this.serviceRegistry = serviceRegistry;
        this.representer = new Representer();
        this.representer.getPropertyUtils().setSkipMissingProperties(true);
    }

    public ConfigServiceImpl(InternalServiceRegistry serviceRegistry, String path) {
        this.path = path;
        this.serviceRegistry = serviceRegistry;
        this.representer = new Representer();
        this.representer.getPropertyUtils().setSkipMissingProperties(true);
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        try {
            Path configPath = Paths.get(path);
            if (Files.notExists(configPath)) {
                if (Files.notExists(configPath.getParent()))
                    Files.createDirectory(configPath.getParent());
                Files.createFile(configPath);
                throw new ConfigCheckException("ConfigCheck failed because path '%s' is unknown, please create config first!", configPath);
            }
            this.configAsString = Files.readString(configPath);
            if (this.configAsString.isEmpty()) {
                throw new ConfigCheckException("ConfigCheck failed because config.yaml is empty, please fill out config first!");
            }
            this.config = this.getConfig(GatewayConfig.class);
            logger.info("Loaded config: {} {}", System.lineSeparator(), this.configAsString);
            logger.info("ConfigService successfully started!");
        } catch (Exception e) {
            this.serviceRegistry.exceptionService()
                    .handleException(new ServiceStartupException("The ConfigService could not be started!", e, this));
        }
    }

    @Override
    public void stop() {
        logger.info("ConfigService successfully shut down!");
    }

    @Override
    public GatewayConfig getConfig() {
        return this.config;
    }

    @Override
    public <T extends Config> T getConfig(Class<T> tClass) throws ConfigException {
        Yaml yaml = new Yaml(new CustomClassLoaderConstructor(tClass.getClassLoader()), this.representer);
        T t = yaml.loadAs(this.configAsString, tClass);
        t.configCheck();
        t.postConstruct();
        return t;
    }

    @Override
    public <T extends Config> T getConfigFromBase(String identifier, Class<T> tClass) throws ConfigException {
        Yaml yaml = new Yaml(new CustomClassLoaderConstructor(tClass.getClassLoader()), this.representer);
        Map<String, Object> configAsMap = yaml.load(this.configAsString);
        T t = yaml.loadAs(yaml.dump(configAsMap.get(identifier)), tClass);
        t.configCheck();
        t.postConstruct();
        return t;
    }

}
