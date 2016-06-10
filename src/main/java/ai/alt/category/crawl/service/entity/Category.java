/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.alt.category.crawl.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author thoqbk
 */
@Entity
@Table(name = "alt_category")
public class Category implements Serializable {

    @Id
    @GeneratedValue
    private long id = -1;

    @Column(name = "status")
    private String status;

    @Column(name = "parent_id")
    private long parentId;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;
    
    @Column(name = "hash")
    private String hash;

    @Transient
    private List<Category> children = new ArrayList<>();

    public long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public long getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }
    
    
}
