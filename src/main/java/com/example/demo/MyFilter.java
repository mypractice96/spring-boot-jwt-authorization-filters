package com.example.demo;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@Component
public class MyFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {

        String uri = httpServletRequest.getRequestURI();
        if (!uri.equals("/login") || !uri.contains("h2") ||  !uri.contains("fav")) {

            String username = jwtUtil.extractUsername(httpServletRequest.getHeader("token"));
            String roleType = jdbcTemplate.queryForObject("select  roleType from endpointRoleType where endpoint=?", String.class, new Object[]{uri});

            if (roleType.equals("PROJECT")) {
                String projectName = getProjectName(httpServletRequest);
                if (!isAuthorized(uri, username, projectName)) {
                    httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            } else if (roleType.equals("ORG")) {
                if (!isAuthorized(uri, username)) {
                    httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

    private String getProjectName(HttpServletRequest httpServletRequest) throws IOException {
        String projectName = "";
        if(httpServletRequest.getMethod().equals("GET")){
            projectName = httpServletRequest.getParameter("projectName");
        }
        else{
            try{
                BufferedReader reader = httpServletRequest.getReader();
                ObjectMapper mapper = new ObjectMapper();
                JSONObject inputJson = new JSONObject(mapper.writeValueAsString(mapper.readValue(reader,Object.class)));
                projectName = (String) inputJson.get("projectName");
                System.out.println("Project Name "+projectName);
                //System.out.println(mapper.writeValueAsString(o));
            }catch (JsonMappingException e){
                e.printStackTrace();
            }
        }
        return projectName;
    }

    private boolean isAuthorized(String uri, String username){
        String role = jdbcTemplate.queryForObject("select role from userRoles where username = ?",String.class,new Object[]{username});
        System.out.println("Role = "+role);
        int rows = jdbcTemplate.queryForObject("select count(*) from permissions where endpoint=? and role =?",Integer.class,new Object[]{uri,role});
        if(rows > 0)
            return true;
        else
            return false;
    }

    private boolean isAuthorized(String uri, String username, String projectName){
        String role = jdbcTemplate.queryForObject("select role from projectRoles where username = ? and projectName=?",String.class,new Object[]{username,projectName});
        System.out.println("Role = "+role);
        int rows = jdbcTemplate.queryForObject("select count(*) from permissions where endpoint=? and role =?",Integer.class,new Object[]{uri,role});
        if(rows > 0)
            return true;
        else
            return false;
    }

}
