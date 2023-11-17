package com.atguigu.gulimall.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class RequestNotifyFilter implements GlobalFilter, Ordered {
    private static Long begin;
    private static Long end;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange , GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().toString();
        String method=request.getMethod().name();

        log.debug("Request =>[{} {}]",method,url);
        begin=System.currentTimeMillis();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            end=System.currentTimeMillis();
            log.debug("Request completed, runtime:{}ms",end-begin);
        }));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
