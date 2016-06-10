/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.alt.category.service;

import java.util.Set;

/**
 *
 * @author thoqbk
 */
public interface CategoryService {
    
    public void setNamespace(String namespace);
    
    public void link(String parent, String child);
    
    /**
     * Number of children
     * @param parent
     * @return 
     */
    public long count(String parent);
    
    /**
     * Number of pages
     * @param parent
     * @param pageSize
     * @return 
     */
    public long count(String parent, int pageSize);
    
    /**
     * Number of key
     * @return 
     */
    public long count();
    
    public Set<String> get(String parent, int pageId, int pageSize);
    
    
    
}
