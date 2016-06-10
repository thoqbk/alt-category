/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.alt.category.crawl.service;

import ai.alt.category.crawl.service.entity.Category;
import java.util.List;

/**
 *
 * @author thoqbk
 */
public interface WikiService {
    public List<Category> read(String wikiUrl);
}
