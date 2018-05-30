import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "my.properties")
public class MyConfigurationProperties {

    @NotNull
    private String someMandatoryProperty;

    private String someOptionalProperty;

    private String someDefaultProperty = "default value";

    // many more...
}