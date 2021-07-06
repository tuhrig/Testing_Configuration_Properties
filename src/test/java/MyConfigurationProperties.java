import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "my.properties")
public class MyConfigurationProperties {

    @NotNull
    @Length(min = 4)
    private String someMandatoryProperty;

    private String someOptionalProperty;

    private String someDefaultProperty = "default value";

    // many more...
}