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
    private ApplicationContext context;//Context of the Whole Application similar as we use in Spring Application Context

//Verifying the JWT token
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*What we get from the client side is in the form
         ("Bearer [space] eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhc2h3aW5pY2hhdHVydmVkaTg5MjRAZ21haWwuY29tIiwiaWF0IjoxNzQ1MjM3ODM1LCJleHAiOjE3NDUyMzc4NzF9.CZFmzuh9aKAKr33JIgC2P2VqB7UZmDq9U-VbSz1W-s0")
         Bearer is written and then the actual token is written and there is space in between them
         e.g.: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhc2h3aW5pY2hhdHVydmVkaTg5MjRAZ21haWwuY29tIiwiaWF0IjoxNzQ1MjM3ODM1LCJleHAiOjE3NDUyMzc4NzF9.CZFmzuh9aKAKr33JIgC2P2VqB7UZmDq9U-VbSz1W-s0
        */

        String authHeader=request.getHeader("Authorization");//Bearer token will be in the Header with the property name "Authorization"
        String token=null;
        String adminEmailId=null;

        if(authHeader!=null && authHeader.startsWith("Bearer")){//if the header has a field named Authorization in the header and it has a Bearer token which starts with Bearer
            token=authHeader.substring(7);//Exclude the "Bearer "
            adminEmailId=this.jwtService.extractEmailIdFromToken(token);
        }


        //if the user is already authenticated then "SecurityContextHolder.getContext().getAuthentication()" this will not be null so then don't authenticate it again and again
        if(adminEmailId!=null && SecurityContextHolder.getContext().getAuthentication()==null){

            //context is the Object of ApplicationContext type which gives a Context of the whole Application
            //we will acquire a bean of type AdminUserDetailsService which we have created and then using the method we get a UserDetailsService Object
            UserDetails userDetails=context.getBean(AdminUserDetailService.class)
                                            .loadUserByUsername(adminEmailId);

            /*
            This block:
                Validates the JWT.
                Creates an
                Sets it into the security context.

                This enables Spring Security to recognize the user as authenticated for the rest of the request lifecycle.
            */
            if(this.jwtService.validateToken(token,userDetails)){

                //This creates an authenticated token representing the user.
                UsernamePasswordAuthenticationToken authToken=
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                //This attaches additional details to the authentication token.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);//This sets the authentication into the SecurityContext, so that the user is considered authenticated for the current request.
                //From this point on, Spring Security will treat this user as logged in.
            }
        }
        filterChain.doFilter(request,response);

    }
}
