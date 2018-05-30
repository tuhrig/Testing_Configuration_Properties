import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConfigurationPropertiesBuilder<T> {

    private T object;
    private String fileName;
    private String prefix;
    private Validator validator;
    private Properties properties = new Properties();
    private List<String> propertiesToRemove = new ArrayList<>();

    public static <T> ConfigurationPropertiesBuilder<T> builder() {
        return new ConfigurationPropertiesBuilder<T>();
    }

    public ConfigurationPropertiesBuilder<T> populate(T object) {
        this.object = object;
        return this;
    }

    public ConfigurationPropertiesBuilder<T> fromFile(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ConfigurationPropertiesBuilder<T> withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public ConfigurationPropertiesBuilder<T> validateUsing(Validator validator) {
        this.validator = validator;
        return this;
    }

    public ConfigurationPropertiesBuilder<T> withProperty(String key, String value) {
        properties.setProperty(key, value);
        return this;
    }

    public ConfigurationPropertiesBuilder<T> withoutProperty(String key) {
        propertiesToRemove.add(key);
        return this;
    }

    public T build() throws BindException {

        Properties propertiesFromFile = loadYamlProperties(fileName);
        propertiesToRemove.forEach(properties::remove);
        propertiesToRemove.forEach(propertiesFromFile::remove);

        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addLast(new PropertiesPropertySource("properties", properties));
        propertySources.addLast(new PropertiesPropertySource("propertiesFromFile", propertiesFromFile));

        PropertiesConfigurationFactory<T> configurationFactory = new PropertiesConfigurationFactory<>(object);
        configurationFactory.setPropertySources(propertySources);
        configurationFactory.setTargetName(prefix);
        configurationFactory.setValidator(validator);
        configurationFactory.bindPropertiesToTarget();

        return object;
    }

    private Properties loadYamlProperties(String fileName) {
        Resource resource = new ClassPathResource(fileName);
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(resource);
        return factoryBean.getObject();
    }
}
