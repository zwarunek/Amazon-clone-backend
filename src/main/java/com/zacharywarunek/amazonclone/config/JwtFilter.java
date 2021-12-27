package com.zacharywarunek.amazonclone.config;

import com.zacharywarunek.amazonclone.account.AccountService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtFilter extends OncePerRequestFilter {

  @Autowired private AccountService accountService;

  @Autowired private JwtUtil jwtTokenUtil;

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    String header = req.getHeader(Constants.HEADER_STRING);
    String username = null;
    String authToken = null;
    if (header != null && header.startsWith(Constants.TOKEN_PREFIX)) {
      authToken = header.replace(Constants.TOKEN_PREFIX, "");
      try {
        username = jwtTokenUtil.getUsernameFromToken(authToken);
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails;
      try {
        userDetails = accountService.loadUserByUsername(username);
      } catch (UsernameNotFoundException e) {
        res.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return;
      }

      if (jwtTokenUtil.validateToken(authToken, userDetails)) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    chain.doFilter(req, res);
  }
}
