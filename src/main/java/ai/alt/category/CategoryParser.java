/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.alt.category;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author thoqbk
 */
public class CategoryParser {

    private static final String categoriesPattern = "(\\[\\[\\:?Category\\:[^\\]]+\\]\\][\\n\\r\\s]*)+";

    /**
     * Examples:
     *
     */
    private static final String categoryPattern = "\\[\\[\\:?(Category\\:[^\\|\\]]+)[^\\]]*\\]\\]";

    public List<String> parse(String text) {
        List<String> retVal = new ArrayList<>();
        if( text == null || text.length() == 0 ){
            return retVal;
        }
        //ELSE:
        Matcher categoriesMatcher = Pattern.compile(categoriesPattern, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE)
                .matcher(text);
        String categoriesInString = null;
        while (categoriesMatcher.find()) {
            categoriesInString = categoriesMatcher.group();
        }
        if (categoriesInString == null || categoriesInString.length() == 0) {
            return retVal;
        }
        //ELSE:
        Matcher categoryMatcher = Pattern.compile(categoryPattern, Pattern.CASE_INSENSITIVE)
                .matcher(categoriesInString);
        while (categoryMatcher.find()) {
            String category = categoryMatcher.group(1);
            retVal.add(toStdCategory(category));
        }
        //return
        return retVal;
    }

    public String toStdCategory(String category) {
        return category.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase()
                .replaceAll("^category\\s*\\:\\s*", "category:");
    }
}
