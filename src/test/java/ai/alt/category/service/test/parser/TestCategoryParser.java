/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.alt.category.service.test.parser;

import ai.alt.category.CategoryParser;
import ai.alt.category.WikiXmlFileParser;
import ai.alt.category.service.CategoryService;

import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

/**
 *
 * @author thoqbk
 */
public class TestCategoryParser {

    @Test
    public void test() throws IOException {

        Multimap<String, String> fileNCategories = ArrayListMultimap.create();
        fileNCategories.putAll("category-1", Arrays.asList("category:people", "category:humans", "category:main topic classifications"));
        fileNCategories.putAll("category-2", Arrays.asList("category:categories by parameter", "category:people", "category:container categories"));
        fileNCategories.putAll("category-3", Arrays.asList("category:top-level stub categories", "category:biography articles needing attention"));
        fileNCategories.putAll("category-4", new ArrayList<String>());

        CategoryParser parser = new CategoryParser();

        for (String file : fileNCategories.keySet()) {
            String fileContent = Resources.toString(TestCategoryParser.class.getResource("/ai/alt/category/resources/test/" + file), Charsets.UTF_8);

            List<String> categories = parser.parse(fileContent);

            assertThat(categories, is(fileNCategories.get(file)));
        }
    }

    @Test
    public void testWikiXmlFileParser() throws SAXException, IOException {
        PropertyConfigurator.configure(TestCategoryParser.class.getResource("/ai/alt/category/resources/log4j.properties"));
        ApplicationContext context = new ClassPathXmlApplicationContext("/ai/alt/category/resources/spring-context.xml");

        WikiXmlFileParser parser = context.getBean(WikiXmlFileParser.class);
        parser.parse(TestCategoryParser.class.getResourceAsStream("/ai/alt/category/resources/test/Wikipedia-20160609112959.xml"));
        parser.parse(TestCategoryParser.class.getResourceAsStream("/ai/alt/category/resources/test/Wikipedia-20160609102525.xml"));

        CategoryService categoryService = context.getBean(CategoryService.class);

        hasLink(categoryService, "category:humans", "category:people");
        hasLink(categoryService, "category:society", "category:people");
        hasLink(categoryService, "category:main topic classifications", "category:people");

        hasLink(categoryService, "Category:Non-fiction literature", "Category:Biography (genre)");
        hasLink(categoryService, "Category:Genres", "Category:Biography (genre)");
        hasLink(categoryService, "Category:People", "Category:Biography (genre)");

        hasLink(categoryService, "Category:Concepts in ethics", "Person");
        hasLink(categoryService, "Category:Humans", "Person");
        hasLink(categoryService, "Category:People", "Person");
        hasLink(categoryService, "Category:Personal life", "Person");
        hasLink(categoryService, "Category:Self", "Person");

    }

    @Test
    public void generateHash() {
        HashFunction hashFunction = Hashing.sha1();
        HashCode hashCode = hashFunction.newHasher()
                .putString("https://en.wikipedia.org/wiki/category:people".toLowerCase(), Charsets.UTF_8)
                .hash();
        String hash = BaseEncoding.base16().lowerCase().encode(hashCode.asBytes());
        System.out.println("_" + hash + "_");
    }

    private void hasLink(CategoryService categoryService, String parent, String child) {
        Set<String> categories = categoryService.get(parent.toLowerCase(), 0, 0);
        String stdChild = child;
        if (child.toLowerCase().startsWith("category:")) {
            stdChild = child.toLowerCase();
        }
        assertThat(categories, hasItem(stdChild));
    }

}
