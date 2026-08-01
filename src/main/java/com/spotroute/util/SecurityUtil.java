package com.spotroute.util;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotroute.config.AppContext;
import com.spotroute.core.enums.Status;
import com.spotroute.persistence.entity.User;
import com.spotroute.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final UserRepository userRepository;

    public static User getLoggedInUser(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(token != null){
            String[] details = token.split("\\.");
            byte[] decodeByte = Base64.getDecoder().decode(details[1]);
            String decodeValue = new String(decodeByte, StandardCharsets.UTF_8);
            Map<String, Object> loggedInUser = stringToObject(decodeValue);
            SecurityUtil util = AppContext.getBean(SecurityUtil.class);
            return util.returnLoggedInUser(loggedInUser.get("id"));
        }
        return null;
    }

    public User returnLoggedInUser(Object id){
        User user = userRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != Status.ACTIVE) {
            throw new RuntimeException("User account is deactivated");
        }

        return user;
    }

    public static User getLoggedInUserFromContext() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new RuntimeException("No current HTTP request found");
        }
        HttpServletRequest request = attrs.getRequest();
        return getLoggedInUser(request);
    }
    public static Map<String, Object> stringToObject(String decodeValue){
        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonObject = null ;
        try {
            jsonObject = mapper.readValue(decodeValue, typeRef);
        } catch (Exception e) {
            System.out.println("Three might be some issue with the JSON string");
        }
        return jsonObject;
    }

}
