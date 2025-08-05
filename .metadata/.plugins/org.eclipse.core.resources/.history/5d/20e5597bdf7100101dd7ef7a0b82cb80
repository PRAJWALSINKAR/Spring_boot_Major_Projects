package prajwal.in.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session != null) {
            String role = (String) session.getAttribute("role");
            if ("ADMIN".equals(role)) {
                return true; // allow
            }
        }

        // Block access
        response.sendRedirect("/403");
        return false;
    }
}
