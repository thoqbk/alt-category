package ai.alt.category.crawl.service.impl;

import ai.alt.category.crawl.service.WikiService;
import ai.alt.category.crawl.service.entity.Category;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author thoqbk
 */
public class WikiServiceImpl implements WikiService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(WikiServiceImpl.class);

    @Override
    public List<Category> read(String wikiUrl) {
        List<Category> retVal = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(wikiUrl)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .timeout(10000)
                    .get();

            Elements categoryLinks = doc.select("a.CategoryTreeLabel");

            retVal.addAll(elementsToCategories(categoryLinks, wikiUrl));

            Elements pagesInCategoryContainer = doc.select("#mw-pages > div.mw-content-ltr");

            if (!pagesInCategoryContainer.isEmpty()) {
                Elements pageLinks = pagesInCategoryContainer.select("a");
                retVal.addAll(elementsToCategories(pageLinks, wikiUrl));
            }
        } catch (IOException ex) {
            logger.error(null, ex);
            throw new RuntimeException(ex);
        }
        return retVal;
    }

    private static List<Category> elementsToCategories(Elements elements, String wikiUrl) {
        List<Category> retVal = new ArrayList<>();
        for (int idx = 0; idx < elements.size(); idx++) {
            Element element = elements.get(idx);

            String shortLink = element.attr("href");
            String url = "https://en.wikipedia.org" + shortLink;
            
            if(wikiUrl.equals(url)){
                continue;
            }
            
            String name = element.ownText();
            
            Category category = new Category();
            category.setName(name);
            category.setUrl(url);

            HashFunction hashFunction = Hashing.sha1();
            HashCode hashCode = hashFunction.newHasher()
                    .putString(category.getUrl().toLowerCase(), Charsets.UTF_8)
                    .hash();
            String hash = BaseEncoding.base16().lowerCase().encode(hashCode.asBytes());
            category.setHash(hash);

            if (shortLink.startsWith("/wiki/Category:")) {
                category.setStatus("pending");
            } else {
                category.setStatus("done");
            }

            retVal.add(category);
        }
        return retVal;
    }

}
