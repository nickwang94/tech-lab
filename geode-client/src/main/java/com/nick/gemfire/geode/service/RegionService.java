package com.nick.gemfire.geode.service;

import org.apache.geode.cache.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RegionService {
    @Autowired
    @Qualifier("exampleRegion")
    Region<String, String> clientRegionFactoryBean;

    public String getRegionValue(String key) {
        return clientRegionFactoryBean.get(key);
    }

    public String putRegionEntity(String key, String value) {
        return clientRegionFactoryBean.put(key, value);
    }
}
