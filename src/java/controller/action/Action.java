/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.action;

import controller.ConfigManager;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.dao.Dao;
import model.dao.DaoEnum;
import model.dao.DaoFactory;
import model.dao.EntityCreator;
import model.dao.ServerOverloadedException;
import model.entity.Order;
import model.entity.User;
import org.apache.log4j.Logger;

/**
 * Action abstract class
 * @author Sasha
 */
public abstract class Action {
    
    /** log4j logger */
    protected final Logger logger = Logger.getLogger(Action.class);
    
    /** http servlet request */
    protected HttpServletRequest request;
    
    /** http servlet response */
    protected HttpServletResponse response;
    
    /** http session */
    protected HttpSession session;
    
    /** dao factory object */
    protected final DaoFactory daoFactory = new DaoFactory();
    
    /**
     * Send redirect to some get action
     * @param message message if it is required in some case
     * @param errorMessage error message if it is required in some case
     * @param linkKey key value of the link in the property file
     * @throws ServletException 
     * @throws IOException 
     */
    protected void sendRedirect(String message, String errorMessage, String linkKey) 
            throws ServletException, IOException {
        setMessages(message, errorMessage);
        if (linkKey == null || linkKey.isEmpty()) {
            linkKey = "link.home";
        } 
        String link = ConfigManager.getProperty(linkKey);
        response.sendRedirect(request.getContextPath() + link);
    }
    
    /**
     * Send redirect to the same page
     * @param message message if it is required in some case
     * @param errorMessage error message if it is required in some case
     * @throws ServletException 
     * @throws IOException 
     */
    protected void sendRedirect(String message, String errorMessage)
            throws ServletException, IOException {
        setMessages(message, errorMessage);
        String path = request.getHeader("Referer");
        if (path == null) {
            path = request.getContextPath() 
                    + ConfigManager.getProperty("link.homepage");
        }
        response.sendRedirect(path);
    }
    
    /**
     * Redirecting with parameter
     * @param actionLink link for corresponde action
     * @param param request parameter name
     * @param value request parameter name value
     * @throws ServletException
     * @throws IOException 
     */
    protected void sendRedirectWithParam(String actionLink, String param, 
            String value) throws ServletException, IOException {
        String link = ConfigManager.
                getProperty(actionLink) + "&" + param + "=" + value;
        response.sendRedirect(request.getContextPath() + link);
    }
    
    /**
     * Set messages if they exist
     * @param message message
     * @param errorMessage error message
     */
    protected void setMessages(String message, String errorMessage) {
        if (message != null && !message.equals("")) {
            session.setAttribute("message", message);
        }
        if (errorMessage != null && !errorMessage.equals("")) {
            session.setAttribute("errorMessage", errorMessage);
        }
    }
    
    /**
     * Get order by id
     * 
     * @param orderId order id
     * @return order object
     * @throws ServletException
     * @throws IOException 
     */
    protected Order getOrderById(int orderId) throws ServletException, 
            IOException {
        Dao orderCreator = daoFactory.getCreator(DaoEnum.ORDER_CREATOR);
        try {
            return (Order) ((EntityCreator) orderCreator).getEntityById(orderId);
        } catch (SQLException e) {
            logger.info(e.getMessage());
            sendRedirect(null, "exception.errormessage.sqlexception");
        } catch (ServerOverloadedException e) {
            logger.info(e.getMessage());
            sendRedirect(null, "exception.errormessage.serveroverloaded");
        }
        return null;
    }
    
    /**
     * Get user by id
     * @param userId user id
     * @return user object
     * @throws ServletException
     * @throws IOException 
     */
    protected User getUserById(int userId) throws ServletException, IOException {
        Dao userCreator = daoFactory.getCreator(DaoEnum.USER_CREATOR);
        try {
            return (User) ((EntityCreator) userCreator).getEntityById(userId);
        } catch (SQLException e) {
            logger.info(e.getMessage());
            sendRedirect(null, "exception.errormessage.sqlexception", 
                    "getAllOrders");
        } catch (ServerOverloadedException e) {
            logger.info(e.getMessage());
            sendRedirect(null, "exception.errormessage.serveroverloaded", 
                    "getAllOrders");
        }
        return null;
    }
    
}
