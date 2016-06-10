/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.alt.category.crawl.service.impl;

import ai.alt.category.crawl.service.CategoryService;
import ai.alt.category.crawl.service.entity.Category;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author thoqbk
 */
public class CategoryServiceImpl implements CategoryService {

    private final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override
    public long enqueue(Category category) {
        long retVal = -1;        
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        try {
            if (category.getId() == -1) {//create
                entityManager.persist(category);
                retVal = category.getId();
            } else if (category.getId() > 0) {//update
                entityManager.merge(category);
                retVal = category.getId();
                //all children
                if (!category.getChildren().isEmpty()) {

                    List<String> hashes = new ArrayList<>();
                    List<Category> duplicatedChildren = null;
                    for (Category child : category.getChildren()) {
                        if ("pending".equals(child.getStatus())) {
                            hashes.add(child.getHash());
                        }
                    }
                    if (!hashes.isEmpty()) {
                        Query hashQuery = entityManager.createQuery("SELECT c FROM Category c WHERE c.hash in :hashes");
                        hashQuery.setParameter("hashes", hashes);
                        duplicatedChildren = hashQuery.getResultList();
                    }

                    for (Category child : category.getChildren()) {
                        if ("pending".equals(child.getStatus())) {
                            boolean duplicated = false;
                            for (Category duplicatedChild : duplicatedChildren) {
                                if (duplicatedChild.getHash().equals(child.getHash())) {
                                    duplicated = true;
                                    break;
                                }
                            }
                            if (duplicated) {
                                child.setStatus("done");
                                logger.info("Found duplicated category, just avoid crawling it. " + category.getHash());
                            }
                        }
                        child.setParentId(retVal);
                        entityManager.persist(child);
                    }
                }
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException("Enqueue fail", e);
        } finally {
            entityManager.close();
        }

        return retVal;
    }

    @Override
    public Category dequeue() {
        Category retVal = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Query query = entityManager.createQuery("SELECT c FROM Category c WHERE c.status = :pending OR c.status = :fail ORDER BY c.id ASC");
            query.setParameter("pending", "pending");
            query.setParameter("fail", "fail");

            query.setFirstResult(0);
            query.setMaxResults(1);

            List result = query.getResultList();
            if (!result.isEmpty()) {
                retVal = (Category) result.get(0);
            }

        } finally {
            entityManager.close();
        }
        //return
        return retVal;
    }

    @Override
    public Category get(long id) {
        Category retVal = null;
        
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            retVal = entityManager.find(Category.class, id);
            if(retVal != null){
                Query query = entityManager.createQuery("SELECT c FROM Category c WHERE c.parentId = :parentId");
                query.setParameter("parentId", id);
                retVal.getChildren().addAll(query.getResultList());
            }
        } finally {
            entityManager.close();
        }
        
        
        return retVal;
    }

}
