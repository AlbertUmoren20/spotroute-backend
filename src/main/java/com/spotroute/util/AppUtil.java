package com.spotroute.util;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.stereotype.Component;

@Data
@Component
@Slf4j
public class AppUtil {
    private static WebServerApplicationContext context;
    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

    public static void setStartTime() {
        startTime.set(System.currentTimeMillis());
    }

    public static String stopTimer() {
        Long start = startTime.get();
        startTime.remove();
        return start != null ? String.valueOf(System.currentTimeMillis() - start) : "0";
    }
}
