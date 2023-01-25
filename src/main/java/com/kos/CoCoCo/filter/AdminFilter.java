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

@WebFilter(urlPatterns = "/admin/*")
public class AdminFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletResponse httpRes = (HttpServletResponse)response;
		HttpServletRequest httpReq = (HttpServletRequest)request;
		
		HttpSession session = httpReq.getSession();
		TeamUserVO teamUser = (TeamUserVO) session.getAttribute("teamUser");
		
		if(teamUser == null) {
			httpRes.setContentType("text/html; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.println("<script>alert('관리자 페이지 접근이 불가능합니다!');  location.href='/CoCoCo';</script>");
			writer.close();
		} else {
			if(teamUser.getUserRole().equals("ADMIN")) {
				chain.doFilter(request, response);
			} else {
				httpRes.setContentType("text/html; charset=UTF-8");
				PrintWriter writer = response.getWriter();
				writer.println("<script>alert('관리자 페이지 접근이 불가능합니다!');  location.href='/main';</script>");
				writer.close();
			}
		}
	}

}
