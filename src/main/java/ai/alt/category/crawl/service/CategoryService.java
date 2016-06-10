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
public interface CategoryService {
    
    /**
     * Category and its children
     * 
     * @param category
     * @return 
     */
    public long enqueue(Category category);
    
    /**
     * Dequeue pending or fail Category
     * @return 
     */
    public Category dequeue();    
    
    public Category get(long id);
    
}
