package com.DTEC.Document_Tracking_and_E_Clearance.configuration;

import com.DTEC.Document_Tracking_and_E_Clearance.exception.UnauthorizedException;
import com.DTEC.Document_Tracking_and_E_Clearance.token.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    private final HandlerExceptionResolver handlerExceptionResolver;

    private final TokenRepository tokenRepository;

    @Value("${header}")
    private String HEADER;

    public JwtAuthenticationFilter(
            UserDetailsService userDetailsService,
            JwtService jwtService,
            HandlerExceptionResolver handlerExceptionResolver,
            TokenRepository tokenRepository
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try {
            final String authHeader = request.getHeader(HEADER);
            final String jwt;
            final String username;

            // this allows to authenticate and refresh user's access token
            // without validating the token's expiration and type
            if(request.getServletPath().contains("/api/v1/auth")){
                filterChain.doFilter(request, response);
                return;
            }

            if(authHeader == null || !authHeader.startsWith("Bearer ")){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set 401 status for expired token
                response.getWriter().write("Invalid Access Token");
                filterChain.doFilter(request, response);
                return;
            }

            jwt = authHeader.substring(7);

            if(this.tokenRepository.findByAccessToken(jwt).isPresent())
                throw new UnauthorizedException("This access token is not valid anymore");

            username = jwtService.extractUsername(jwt);
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if(jwtService.isTokenValid(jwt, userDetails)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException ex) {
            // Correctly handle expired tokens
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set 401 status for expired token
            response.getWriter().write("JWT token has been expired");
        } catch (Exception e){
            this.handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }
}
