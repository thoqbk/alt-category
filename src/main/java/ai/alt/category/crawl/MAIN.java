/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.alt.category.crawl;

import ai.alt.category.crawl.service.CategoryService;
import ai.alt.category.crawl.service.WikiService;
import ai.alt.category.crawl.service.entity.Category;

import java.io.IOException;
import java.util.List;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author thoqbk
 */
public class MAIN {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MAIN.class);

    public static void main(String[] args) throws IOException {        
        PropertyConfigurator.configure(MAIN.class.getResource("/ai/alt/category/resources/log4j.properties"));

        logger.info("Starting application");

        ApplicationContext context = new ClassPathXmlApplicationContext("/ai/alt/category/resources/crawl/spring-context.xml");

        WikiFetcher fetcher = new WikiFetcher(context.getBean(CategoryService.class), context.getBean(WikiService.class));
        fetcher.run();
    }

    private static class WikiFetcher implements Runnable {

        private final WikiService wikiService;
        private final CategoryService categoryService;

        public WikiFetcher(CategoryService categoryService, WikiService wikiService) {
            this.wikiService = wikiService;
            this.categoryService = categoryService;
        }

        @Override
        public void run() {
            while (true) {
                Category category = categoryService.dequeue();
                if (category == null) {
                    logger.info("Finished!");
                    return;
                }
                //ELSE:
                try {
                    List<Category> children = wikiService.read(category.getUrl());
                    for (Category child : children) {
                        child.setParentId(category.getId());
                    }
                    category.setStatus("done");
                    category.getChildren().clear();
                    category.getChildren().addAll(children);
                    categoryService.enqueue(category);
                } catch (Exception e) {
                    category.setStatus("fail");
                    categoryService.enqueue(category);
                    logger.error(null, e);
                }
            }
        }

    }

}
