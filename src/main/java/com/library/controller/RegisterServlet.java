package com.library.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.library.model.Users;
import com.library.repository.UserRepository;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private UserRepository userRepo = new UserRepository();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    Users user = new Users();
	    user.setUsername(request.getParameter("username"));
	    user.setEmail(request.getParameter("email"));
	    user.setPassword(request.getParameter("password"));

	    HttpSession session = request.getSession();
	    if (userRepo.save(user)) {
	        session.setAttribute("successMsg", "Registration Successful!");
	        response.sendRedirect("login.jsp");
	    } else {
	        // Duplicate ဖြစ်ရင်ဖြစ်ဖြစ်၊ Error တက်ရင်ဖြစ်ဖြစ် ဒီ message ကို ပြမယ်
	        session.setAttribute("errorMsg", "Username or Email already exists!");
	        response.sendRedirect("register.jsp");
	    }
	}

}
