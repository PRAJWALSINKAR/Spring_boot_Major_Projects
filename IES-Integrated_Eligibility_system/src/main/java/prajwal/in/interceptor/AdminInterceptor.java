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
	    if (session == null) {
	        response.sendRedirect("/login");
	        return false;
	    }

	    String role = (String) session.getAttribute("role");
	    String uri = request.getRequestURI();

	    // Allow caseworker access to their own profile edit
	    if (uri.equals("/caseworker/edit")) {
	        if ("CASEWORKER".equals(role)) {
	            return true; // Allow caseworker
	        }
	    }

	    // Only allow admin for other caseworker paths
	    if (!"ADMIN".equals(role)) {
	        response.sendRedirect("/access-denied"); // Or 403 page
	        return false;
	    }

	    return true;
	}
}

