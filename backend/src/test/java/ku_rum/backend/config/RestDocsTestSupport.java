package ku_rum.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ku_rum.backend.global.config.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Disabled
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import({RestDocsConfiguration.class})
@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureMockMvc
public class RestDocsTestSupport {

    @Autowired
    protected RestDocumentationResultHandler restDocs;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    protected static Attributes.Attribute constraints( // contraints Attribute 간단하게 추가
                                                       final String value) {
        return new Attributes.Attribute("constraints", value);
    }


    @BeforeEach
    void setUp(final WebApplicationContext context,
               final RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(MockMvcResultHandlers.print()) // print 적용
                .alwaysDo(restDocs) // RestDocsConfiguration 클래스의 bean 적용
                .build();
    }
}