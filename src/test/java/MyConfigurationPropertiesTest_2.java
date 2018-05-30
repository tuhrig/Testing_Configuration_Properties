import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { MyConfigurationPropertiesTest_2.TestConfiguration.class })
@ActiveProfiles("minimal")
public class MyConfigurationPropertiesTest_2 {

    @Autowired
    private MyConfigurationProperties properties;

    @Test
    public void should_Populate_MyConfigurationProperties() {
        assertThat(properties.getSomeMandatoryProperty()).isEqualTo("123456");
        assertThat(properties.getSomeOptionalProperty()).isEqualTo(null);
        assertThat(properties.getSomeDefaultProperty()).isEqualTo("default value");
    }

    @EnableConfigurationProperties(MyConfigurationProperties.class)
    public static class TestConfiguration {
        // nothing
    }
}
