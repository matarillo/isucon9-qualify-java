package isucon9.qualify.web;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final HttpSession session;

    public SessionService(HttpSession session) {
        this.session = session;
    }

    private Optional<String> getStringValueFromSession(String name) {
        Object o = session.getAttribute(name);
        if (o instanceof String) {
            return Optional.of((String)o);
        } else {
            return Optional.empty();
        }
    }

    private void setValueToSession(String name, String value) {
        session.setAttribute(name, value);
    }

    private Optional<Long> getLongValueFromSession(String name) {
        Object o = session.getAttribute(name);
        if (o instanceof Long) {
            return Optional.of((Long)o);
        } else {
            return Optional.empty();
        }
    }

    private void setValueToSession(String name, Long value) {
        session.setAttribute(name, value);
    }

    public String getCsrfToken() {
        return getStringValueFromSession("csrf_token").orElse("");
    }

    public void setCsrfToken(String value) {
        setValueToSession("csrf_token", value);
    }

    public long getUserId() {
        return getLongValueFromSession("user_id").orElse(0L);
    }

    public void setUserId(long value) {
        setValueToSession("user_id", value);
    }
}
