package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "API 접근 로그")
@Aspect
public class AspectConfig {

    // 서로 다른 경로에 있어서 포인트컷 하나로 쓸 수 있는 annotation 기반 포인트컷 사용
    @Pointcut("@annotation(org.example.expert.domain.common.annotation.RequestAPI)")
    private void RequestAPIAnnotation() {}

    @Around("RequestAPIAnnotation()")
    public Object RequestAPI(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, Object> params = new HashMap<>();
        LocalDateTime RequestTime = LocalDateTime.now();

        try{
            params.put("requestUserId", request.getAttribute("userId"));
            params.put("requestTime", RequestTime);
            params.put("requestUri", request.getRequestURI());

            Object result = joinPoint.proceed();
            return result;
        } finally {
            log.info("::: 요청한 사용자 : {}", params.get("requestUserId"));
            log.info("::: 요청 시각 : {}", params.get("requestTime"));
            log.info("::: 요청 URI : {}", params.get("requestUri"));
        }
    }
}
