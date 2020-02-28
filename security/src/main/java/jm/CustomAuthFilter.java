package jm;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class CustomAuthFilter extends GenericFilterBean {

    @Autowired
    private BotService botService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String token= request.getHeader(AUTHORIZATION);
        token= StringUtils.removeStart(token, "Bearer").trim();
        System.out.println("token: " + token);

        boolean isPresent = botService.findByToken(token).isPresent();
        if (isPresent){
            filterChain.doFilter(request, response);
        }
    }
}
