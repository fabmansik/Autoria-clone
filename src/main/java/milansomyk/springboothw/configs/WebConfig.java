package milansomyk.springboothw.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {
    String PATH = System.getProperty("user.home")+File.separator+"adImages"+ File.separator;
    File directory = new File(PATH);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(! directory.exists()){
            directory.mkdir();
        }
        registry.addResourceHandler("/adImages/**")
                .addResourceLocations("file:///"+PATH);
    }
}
