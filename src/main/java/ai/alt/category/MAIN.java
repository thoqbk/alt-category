/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.alt.category;

import java.io.IOException;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

/**
 *
 * @author thoqbk
 */
public class MAIN {
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MAIN.class);
    
    public static void main(String[] args) throws SAXException, IOException{
        String filePath = args[0];
        PropertyConfigurator.configure(MAIN.class.getResource("/ai/alt/category/resources/log4j.properties"));
        ApplicationContext context = new ClassPathXmlApplicationContext("/ai/alt/category/resources/spring-context.xml");
        
        logger.info("Starting Alt+ Category ...");
        
        WikiXmlFileParser parser = context.getBean(WikiXmlFileParser.class);
        parser.parse(filePath);
        
        ((ConfigurableApplicationContext)context).close();
        logger.info("Finish!");
    }
}
