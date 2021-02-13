package pl.coderslab.users;

import pl.coderslab.User;
import pl.coderslab.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UserEdit", urlPatterns = {"/users/edit"})
public class UserEdit extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user= new User();
        user.setId(Integer.parseInt(request.getParameter("id")));
        user.setUserName(request.getParameter("userName"));
        user.setEmail(request.getParameter("userEmail"));
        user.setPassword(request.getParameter("userPassword"));
//        UserDao userDao=new UserDao();
        UserDao.update(user);
        response.sendRedirect("/myCRUD_war_exploded/users/list");


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id=request.getParameter("id");
        UserDao userDao= new UserDao();
        User read= userDao.read(Integer.parseInt(id));
        request.setAttribute("user",read);
        getServletContext().getRequestDispatcher("/users/edit.jsp").forward(request,response);

    }
}
