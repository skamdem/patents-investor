package dev.skamdem.patentsinvestor;

import dev.skamdem.patentsinvestor.controllers.AuthenticationController;
import dev.skamdem.patentsinvestor.controllers.HomeController;
import dev.skamdem.patentsinvestor.data.UserRepository;
import dev.skamdem.patentsinvestor.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kamdem
 * For preventing access to certain paths
 */
public class AuthenticationFilter extends HandlerInterceptorAdapter {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationController authenticationController;

    //General messages
    private final String INFO_MESSAGE_KEY = "message";

    /**
     * List of URLs that shall NOT proceed without authentication
     */
    private static final List<String> blackList = Arrays.asList(
            "/stocks/portfolio",
            "/tags",
            "/stocks/adjust-shares-portfolio",
            "/stocks/add-to-portfolio",
            "/stocks/remove-from-portfolio",
            "/stocks/add-tag",
            "/stocks/remove-tag",
            "/stocks/progress-bar-value",
            "/stocks/callAPIs"
    );

    /**
     * List of URLs that shall proceed without authentication
     */
    private static boolean isBlackListed(String path) {
        for (String pathRoot : blackList) {
            if (path.startsWith(pathRoot)) {
                return true;
            }
        }
        return false;
    }

    private void loadRedirectMessage(HttpSession session) {
        session.setAttribute(INFO_MESSAGE_KEY, "danger|" + HomeController.NOT_LOGGED_IN_MSG);
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException {
        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);
        if (user != null) { // The user is logged in
            return true;
        }

        // Require sign-in for blacklisted pages
        if (!isBlackListed(request.getRequestURI())) {
            return true;
        }

        // The user is NOT logged in AND the URI is blacklisted
        response.sendRedirect("/");
        loadRedirectMessage(request.getSession());

        return false;
    }
}
