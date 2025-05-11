package admin.admin.Filter;

import admin.admin.Service.AdminUserDetailService;
import admin.admin.Service.JWT_Service;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JWT_Filter extends OncePerRequestFilter {

    @Autowired
    private JWT_Service jwtService;

    @Autowired
    private ApplicationContext context;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/oauth2/") || path.startsWith("/login/oauth2/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*What we get from the client side is in the form
         "Bearer" actual token
         Bearer is written and then the actual token is written
         e.g.: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhc2h3aW5pY2hhdHVydmVkaTg5MjRAZ21haWwuY29tIiwiaWF0IjoxNzQ1MjM3ODM1LCJleHAiOjE3NDUyMzc4NzF9.CZFmzuh9aKAKr33JIgC2P2VqB7UZmDq9U-VbSz1W-s0
        */

        String authHeader=request.getHeader("Authorization");//Bearer token will be in the Header
        String token=null;
        String adminEmailId=null;

        if(authHeader!=null && authHeader.startsWith("Bearer")){//if the header has a field named Authorization in the header and it has a Bearer token which starts with Bearer
            token=authHeader.substring(7);//Exclude the "Bearer "
            adminEmailId=this.jwtService.extractEmailIdFromToken(token);
        }

        //if the user is already authenticated then "SecurityContextHolder.getContext().getAuthentication()" this will not be null so then don't authenticate it again and again
        if(adminEmailId!=null && SecurityContextHolder.getContext().getAuthentication()==null){

            UserDetails userDetails=context.getBean(AdminUserDetailService.class)
                                            .loadUserByUsername(adminEmailId);
            if(jwtService.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken authToken=
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);

    }
}
