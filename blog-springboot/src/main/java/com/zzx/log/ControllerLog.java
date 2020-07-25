package com.zzx.log;

import com.zzx.model.pojo.Log;
import com.zzx.service.LogService;
import com.zzx.utils.DateUtil;
import com.zzx.utils.JwtTokenUtil;
import com.zzx.utils.RequestUtil;
import com.zzx.utils.StringUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 控制层 日志 切面
 */
@Aspect
@Component
@Slf4j
public class ControllerLog {


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LogService logService;


    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private DateUtil dateUtil;

    /**
     * 拦截控制层的所有public方法
     */
    @Pointcut("execution(public * com.zzx.controller.*.*(..))")
    public void log() {
    }

    /**
     * 不打印日志的路径
     */
    private static final Set<String> PASS_PATH = new HashSet<>(1);

    static {
        PASS_PATH.add("/user/getMailSendState");
    }

    /**
     * 方法执行前后 拦截
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("log()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        String requestUri = request.getRequestURI();

        //方法消耗时间
        long start = System.currentTimeMillis();
        Object obj = pjp.proceed();
        long end = System.currentTimeMillis();
        long time = end - start;
        if (!PASS_PATH.contains(requestUri)) {
            StringBuilder builder = new StringBuilder();
            builder.append("{URL:[").append(requestUri).append("],")
                    .append("RequestMethod:[").append(request.getMethod()).append("],")
                    .append("Args:").append(Arrays.toString(pjp.getArgs())).append(",")
                    .append("ReturnValue:[").append(obj == null ? "null" : obj.toString()).append("],")
                    .append("Time:[").append(time).append("ms],")
                    .append("MethodName:[").append(pjp.getSignature()).append("]}");
            log.info(builder.toString());
        }

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        // 方法路径
        String methodName = pjp.getTarget().getClass().getName()+"."+signature.getName()+"()";
        //获取注解
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        String annoValue = apiOperation.value();

        StringBuilder params = new StringBuilder("{");
        //参数值
        Object[] argValues = pjp.getArgs();
        //参数名称
        String[] argNames = ((MethodSignature)pjp.getSignature()).getParameterNames();
        if(argValues != null){
            for (int i = 0; i < argValues.length; i++) {
                params.append(" ").append(argNames[i]).append(": ").append(argValues[i]);
            }
        }
        String username = jwtTokenUtil.getUsernameFromRequest(request);
        String ip = requestUtil.getIpAddress(request);

        Log log = new Log();
        log.setCreateTime(dateUtil.getCurrentDate());
        log.setIp(ip);
        log.setUsername(username == null ? "anno" : username);
        log.setAddress(StringUtils.getCityInfo(ip));
        log.setTime(time);
        log.setMethod(methodName);
        log.setParams(params.toString() + " }");
        log.setDescription(annoValue);

        logService.saveLog(log);

        return obj;

    }


}

