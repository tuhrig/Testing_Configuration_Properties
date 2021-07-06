import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ValidationAutoConfiguration.class })
public class MyConfigurationPropertiesTest {
 
    @Autowired
    private LocalValidatorFactoryBean localValidatorFactoryBean;
 
    @Test
    public void should_Populate_MyConfigurationProperties() throws Exception {
 
        MyConfigurationProperties properties = ConfigurationPropertiesBuilder.<MyConfigurationProperties>builder()
                                                             .populate( MyConfigurationProperties.class)
                                                             .fromFile("application.yml")
                                                             .withPrefix("my.properties")
                                                             .validateUsing(localValidatorFactoryBean)
                                                             .withProperty("my.properties.some-optional-property", "abcdef")
                                                             .withProperty("my.properties.some-default-property", "overwritten")
                                                             .build();
 
        assertThat(properties.getSomeMandatoryProperty()).isEqualTo("123456");
        assertThat(properties.getSomeOptionalProperty()).isEqualTo("abcdef");
        assertThat(properties.getSomeDefaultProperty()).isEqualTo("overwritten");
    }
 
    @Test
    public void should_Populate_MyConfigurationProperties_WithMandatoryPropertiesOnly() throws Exception {
 
        MyConfigurationProperties properties = ConfigurationPropertiesBuilder.<MyConfigurationProperties>builder()
                                                             .populate( MyConfigurationProperties.class)
                                                             .fromFile("application.yml")
                                                             .withPrefix("my.properties")
                                                             .validateUsing(localValidatorFactoryBean)
                                                             .build();
 
        assertThat(properties.getSomeMandatoryProperty()).isEqualTo("123456");
        assertThat(properties.getSomeOptionalProperty()).isEqualTo(null);
        assertThat(properties.getSomeDefaultProperty()).isEqualTo("default value");
    }
 
    @Test(expected = BindException.class)
    public void should_ThrowException_IfMandatoryPropertyIsMissing() throws Exception {
        ConfigurationPropertiesBuilder.<MyConfigurationProperties>builder()
                              .populate(MyConfigurationProperties.class)
                              .fromFile("application.yml")
                              .withPrefix("my.properties")
                              .validateUsing(localValidatorFactoryBean)
                              .withoutProperty("my.properties.some_mandatory_property")
                              .withProperty("my.properties.some_optional_property", "123")
                              .build();
    }

    @Test(expected = BindException.class)
    public void should_ThrowException_IfFieldIsTooShort() throws Exception {
        ConfigurationPropertiesBuilder.<MyConfigurationProperties>builder()
                .populate( MyConfigurationProperties.class)
                .fromFile("application.yml")
                .withPrefix("my.properties")
                .validateUsing(localValidatorFactoryBean)
                .withProperty("my.properties.some_mandatory_property", "123")
                .build();
    }
}