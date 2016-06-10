/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.alt.category.server;

import ai.alt.category.crawl.service.CategoryService;
import ai.alt.category.crawl.service.entity.Category;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 *
 * @author thoqbk
 */
@Controller
public class MAIN {
    
    private static final Logger logger = LoggerFactory.getLogger(MAIN.class);
    
    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "/load", method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> load(@RequestParam("id") long id) {
        Map<String, Object> retVal = new HashMap<>();
        retVal.put("id", id);
        
        Category category = categoryService.get(id);
        
        if(category == null){
            retVal.put("status", "fail");            
            return retVal;
        }else{
            retVal.put("status", "successful");
        }
        
        retVal.put("children", category.getChildren());
        category.setChildren(null);
        retVal.put("category", category);
        
        //debug
        logger.debug("Category: " + category.getName());

        return retVal;
    }

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure(MAIN.class.getResource("/ai/alt/category/resources/log4j-console.properties"));
        //spring load
        logger.info("Loading application context");

        String virtualWebDirectoryPath = MAIN.class.getResource("/ai/alt/category/resources/server").toExternalForm();
        final XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setConfigLocation(virtualWebDirectoryPath + "/spring-context.xml");
        final ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        final DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        handler.getServletHandler().addServletWithMapping(new ServletHolder(dispatcherServlet), "/");
        handler.start();

        //Setup and start server
        int serverPort = 80;
        Server server = new Server(new InetSocketAddress("0.0.0.0", serverPort));
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                // must set before handle
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
                
                handler.handle(target, baseRequest, request, response);
            }
        });

        server.start();

        logger.info("Alt Category has started successfully, port " + serverPort);
        System.in.read();
        server.stop();
        //Release resource when application stops
        ((ConfigurableApplicationContext) context).close();
        logger.info("Release resources in application context successfully");
    }
}
