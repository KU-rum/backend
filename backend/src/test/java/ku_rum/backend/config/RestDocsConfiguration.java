package ku_rum.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

@TestConfiguration
public class RestDocsConfiguration {
    @Bean
    public RestDocumentationResultHandler restDocumentationResultHandler() {
        return MockMvcRestDocumentation.document(
                "{class-name}/{method-name}",  // 문서 이름 설정
                preprocessRequest(  // 공통 헤더 설정
                        modifyHeaders()
                                .remove("Content-Length")
                                .remove("Host"),
                        prettyPrint()),  // pretty json 적용
                preprocessResponse(  // 공통 헤더 설정
                        modifyHeaders()
                                .remove("Content-Length")
                                .remove("X-Content-Type-Options")
                                .remove("X-XSS-Protection")
                                .remove("Cache-Control")
                                .remove("Pragma")
                                .remove("Expires")
                                .remove("X-Frame-Options"),
                        prettyPrint())    // pretty json 적용
        );
    }
}
