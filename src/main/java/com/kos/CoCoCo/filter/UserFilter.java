package com.kos.CoCoCo.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kos.CoCoCo.vo.TeamUserVO;

@WebFilter(urlPatterns = {"/main/*", "/admin/*", "/work/*", "/notice/*", "/board/*"})
public class UserFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletResponse httpRes = (HttpServletResponse)response;
		HttpServletRequest httpReq = (HttpServletRequest)request;
		
		HttpSession session = httpReq.getSession();
		
		Long teamId = (Long) session.getAttribute("teamId");
		String getClass = session.getAttribute("user").getClass().getSimpleName();
		
		if(!getClass.equals("UserVO") || teamId == null) {
			httpRes.sendRedirect("/CoCoCo");
		} else {
			chain.doFilter(request, response);
		}
	}
}