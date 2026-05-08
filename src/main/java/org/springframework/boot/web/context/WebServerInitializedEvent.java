package org.springframework.boot.web.context;

import org.springframework.context.ApplicationEvent;

/**
 * Compatibility hack for Spring Cloud 2025.0.0 + Spring Boot 4.0.5
 * Spring Cloud Gateway discovery auto-configurations still reference this class,
 * which was moved/removed in Spring Boot 4.0.
 */
public abstract class WebServerInitializedEvent extends ApplicationEvent {
    protected WebServerInitializedEvent(Object source) {
        super(source);
    }
}
