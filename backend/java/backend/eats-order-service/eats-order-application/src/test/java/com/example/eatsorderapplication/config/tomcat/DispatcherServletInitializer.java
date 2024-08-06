package com.example.eatsorderapplication.config.tomcat;


import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


/**
 * ref: https://github.com/rstoyanchev/spring-websocket-portfolio/blob/main/src/main/java/org/springframework/samples/portfolio/config/DispatcherServletInitializer.java#L24
 */
public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{};
    }

    //    서블릿이 애플리케이션의 루트 경로(/)와 모든 하위 경로를 처리하도록 설정합니다.
    //    즉, 모든 요청이 이 서블릿으로 전달됩니다. 이는 기본적인 설정으로,
    //    웹 애플리케이션에서 모든 요청을 단일 서블릿이 처리하도록 할 때 사용
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    //     서블릿 등록의 초기화 파라미터를 설정
    //    서블릿이 HTTP OPTIONS 요청을 처리할 때 사용됩니다.
    //    HTTP OPTIONS 메소드는 클라이언트가 서버에서 지원하는 HTTP 메소드를 조회하기 위해 사용
    //    일반적으로 CORS(Cross-Origin Resource Sharing)와 같은 상황에서 OPTIONS 요청이 필요
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setInitParameter("dispatchOptionsRequest", "true");
    }

}