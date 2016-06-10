/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.alt.category.service.impl;

import ai.alt.category.service.CategoryService;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 *
 * @author thoqbk
 */
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String COUNT_ALL_KEY = "allKeys:count";
    private String namespace = "alt";

    private final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    public void link(String parent, String child) {
        double score = 1.0;
        if (child.startsWith("category:")) {
            score = 0.0;
        }
        redisTemplate.boundZSetOps(namespace + ":" + parent)
                .add(child, score);
        redisTemplate.boundValueOps(namespace + ":" + COUNT_ALL_KEY)
                .increment(1);

        logger.debug("Linked " + parent + " -> " + child);
    }

    @Override
    public long count(String parent) {
        return redisTemplate.boundZSetOps(namespace + ":" + parent)
                .count(-1.0, 2);
    }

    @Override
    public long count() {
        long retVal = 0;
        Object countAll = redisTemplate.boundValueOps(namespace + ":" + COUNT_ALL_KEY)
                .get();
        if (countAll != null) {
            retVal = Long.parseLong(countAll.toString());
        }
        return retVal;
    }

    @Override
    public Set<String> get(String parent, int pageId, int pageSize) {
        long start = pageId * pageSize;
        long stop = (pageId + 1) * pageSize - 1;
        return redisTemplate.boundZSetOps(namespace + ":" + parent)
                .range(start, stop);
    }

    @Override
    public long count(String parent, int pageSize) {
        long count = count(parent);
        long retVal = count / pageSize;
        if (count % pageSize != 0) {
            retVal++;
        }
        return retVal;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

}
