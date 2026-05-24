/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group3.dto.response.ApiResponse;
import com.group3.utils.JwtUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author THUAN
 */
public class JwtFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        boolean secureApi = requestUri.startsWith(contextPath + "/api/users/secure")
                || requestUri.startsWith(contextPath + "/api/events/organizer");

        if (secureApi) {

            String header = httpRequest.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                sendJsonError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Thiếu hoặc Authorization header không hợp lệ");
                return;
            }

            try {
                String token = header.substring(7);
                String username = JwtUtils.validateTokenAndGetUsername(token);

                if (username != null) {
                    httpRequest.setAttribute("username", username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    chain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                sendJsonError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ hoặc đã hết hạn");
                return;
            }

            sendJsonError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
            return;
        }

        chain.doFilter(request, response);
    }

    private void sendJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<Object> apiResponse = new ApiResponse<>(status, message, null);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

}
