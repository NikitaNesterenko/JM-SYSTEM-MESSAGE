package jm;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

public class JmAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        HttpSession session = httpServletRequest.getSession(true);
        Object o = session.getAttribute("workspace");
        authentication.getAuthorities().forEach(auth -> {
            if (auth.getAuthority().equals("ROLE_OWNER")) {
                try {
                    httpServletResponse.sendRedirect("chooseWorkspace");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        if (httpServletResponse.getStatus() != 302) httpServletResponse.sendRedirect("/admin");
    }
}
