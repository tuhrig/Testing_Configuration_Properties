import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigurationPropertiesBuilder<T> {

    private Class<T> clazz;
    private String fileName;
    private String prefix;
    private Validator validator;
    private final Properties properties = new Properties();
    private final List<String> propertiesToRemove = new ArrayList<>();

    public static <T> ConfigurationPropertiesBuilder<T> builder() {
        return new ConfigurationPropertiesBuilder<T>();
    }

    public ConfigurationPropertiesBuilder<T> populate(Class<T> clazz) {
        this.clazz = clazz;
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

    public T build() {

        Properties propertiesFromFile = loadYamlProperties(fileName);
        propertiesToRemove.forEach(properties::remove);
        propertiesToRemove.forEach(propertiesFromFile::remove);

        PropertiesPropertySource sourceA = new PropertiesPropertySource("properties", properties);
        PropertiesPropertySource sourceB = new PropertiesPropertySource("propertiesFromFile", propertiesFromFile);
        Iterable<PropertySource<?>> list = Arrays.asList(sourceA, sourceB);

        Iterable<ConfigurationPropertySource> propertySource = ConfigurationPropertySources.from(list);
        Binder binder = new Binder(propertySource);

        return binder.bind(
                prefix,
                Bindable.of(clazz),
                new ValidationBindHandler(validator)
        ).get();
    }

    private Properties loadYamlProperties(String fileName) {
        Resource resource = new ClassPathResource(fileName);
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(resource);
        return factoryBean.getObject();
    }
}
