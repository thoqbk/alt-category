/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.alt.category;

import ai.alt.category.service.CategoryService;
import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

/**
 *
 * @author thoqbk
 */
public class WikiXmlFileParser {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WikiXmlFileParser.class);

    @Autowired
    private CategoryService categoryService;

    private final CategoryParser categoryParser = new CategoryParser();

    private final AtomicInteger pagesCount = new AtomicInteger(0);

    public void parse(InputStream inputStream) throws SAXException, IOException {
        WikiXMLParser parser = new WikiXMLParser(inputStream, new ArticleHandler());
        parser.parse();
    }

    public void parse(String filePath) throws SAXException, IOException {
        logger.info("Begin parsing file: " + filePath);
        WikiXMLParser parser = new WikiXMLParser(new File(filePath), new ArticleHandler());
        parser.parse();
    }

    private class ArticleHandler implements IArticleFilter {

        @Override
        public void process(WikiArticle page, Siteinfo siteInfo) throws IOException {
            pagesCount.incrementAndGet();
            if (pagesCount.get() % 10000 == 0) {
                logger.info("Begin processing page " + pagesCount.get());
            }
            if (!page.isCategory() && !page.isMain()) {
                return;
            }
            try {
                String pageContent = page.getText();

                String title = page.getTitle().trim();
                if (page.isCategory()) {
                    title = categoryParser.toStdCategory(title);
                }
                List<String> categories = categoryParser.parse(pageContent);
                for (String category : categories) {
                    categoryService.link(category, title);                    
                }
            } catch (Exception e) {
                logger.error("Processing page: " + page.getTitle(), e);
                throw new RuntimeException(e);
            }
        }
    }

}
