/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.alt.category.service.test;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thoqbk
 */
public class MAIN3 {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MAIN3.class);

    public static void main(String[] args) throws Exception {

        PropertyConfigurator.configure(MAIN3.class.getResource("/ai/alt/category/resources/log4j.properties"));

        logger.info("Starting application");

        String filePath = "/Volumes/Hi/wikidatawiki-20160601-pages-articles3.xml-p007305527p016090523.bz2";
        String outputFilePath = "/Users/thoqbk/Downloads/categories.json";
        if (args != null && args.length > 0) {
            filePath = args[0];
        }
        if (args != null && args.length > 1) {
            outputFilePath = args[1];
        }

        JsonFactory jsonFactory = new JsonFactory();

        final JsonGenerator jsonGenerator = jsonFactory.createGenerator(new File(outputFilePath), JsonEncoding.UTF8);
        jsonGenerator.writeStartArray();

        final AtomicInteger pagesCount = new AtomicInteger(0);
        final AtomicInteger categoriesCount = new AtomicInteger(0);

        WikiXMLParser parser = new WikiXMLParser(new File(filePath), new IArticleFilter() {
            @Override
            public void process(WikiArticle page, Siteinfo siteInfo) throws IOException {
                pagesCount.incrementAndGet();

                if (pagesCount.get() % 10000 == 0) {
                    logger.info("Begin processing page " + pagesCount.get() + ", found " + categoriesCount.get() + " categories");
                }

                if (!page.isCategory()) {
                    return;
                }
                //ELSE:
                logger.info("Found new category page, title: " + page.getTitle() + "; Id: " + page.getId());
                categoriesCount.incrementAndGet();
                
                try {

                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("id", page.getId());
                    jsonGenerator.writeStringField("title", page.getTitle());
                    jsonGenerator.writeStringField("text", page.getText());
                    jsonGenerator.writeEndObject();

                    logger.info("> text: " + page.getText());

                    if (categoriesCount.get() % 20 == 0) {
                        jsonGenerator.flush();
                        logger.info("Flush, categories count: " + categoriesCount.get());
                    }

                } catch (IOException ex) {
                    logger.error(null, ex);
                }
            }
        });

        parser.parse();

        jsonGenerator.writeEndArray();
        jsonGenerator.close();

        logger.info("Total pages: " + pagesCount.get() + ", categories count: " + categoriesCount.get());
    }

}
