/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.soul.plugin.base.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.dromara.soul.common.dto.PluginData;
import org.dromara.soul.common.dto.RuleData;
import org.dromara.soul.common.dto.SelectorData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * The type Base data cache.
 */
public class BaseDataCache {
    
    private static final BaseDataCache INSTANCE = new BaseDataCache();
    /**
     * pluginName -> PluginData.
     */
    private static final ConcurrentMap<String, PluginData> PLUGIN_MAP = Maps.newConcurrentMap();
    
    /**
     * pluginName -> SelectorData.
     */
    private static final ConcurrentMap<String, List<SelectorData>> SELECTOR_MAP = Maps.newConcurrentMap();
    
    /**
     * selectorId -> RuleData.
     */
    private static final ConcurrentMap<String, List<RuleData>> RULE_MAP = Maps.newConcurrentMap();
    
    private BaseDataCache() {
    }
    
    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static BaseDataCache getInstance() {
        return INSTANCE;
    }
    
    /**
     * Cache plugin data.
     *
     * @param data the data
     */
    public void cachePluginData(final PluginData data) {
        PLUGIN_MAP.put(data.getName(), data);
    }
    
    /**
     * Remove plugin data.
     *
     * @param data the data
     */
    public void removePluginData(final PluginData data) {
        PLUGIN_MAP.remove(data.getName());
    }
    
    /**
     * Obtain plugin data plugin data.
     *
     * @param pluginName the plugin name
     * @return the plugin data
     */
    public PluginData obtainPluginData(final String pluginName) {
        return PLUGIN_MAP.get(pluginName);
    }
    
    /**
     * Cache select data.
     *
     * @param data the data
     */
    public void cacheSelectData(final SelectorData data) {
        String key = data.getPluginName();
        if (SELECTOR_MAP.containsKey(key)) {
            List<SelectorData> existList = SELECTOR_MAP.get(key);
            existList.add(data);
            // distinct and sort
            List<SelectorData> resultList = existList.stream().sorted(Comparator.comparing(SelectorData::getSort))
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SelectorData::getId))), ArrayList::new));
            SELECTOR_MAP.put(key, resultList);
        } else {
            SELECTOR_MAP.put(key, Lists.newArrayList(data));
        }
    }
    
    /**
     * Remove select data.
     *
     * @param data the data
     */
    public void removeSelectData(final SelectorData data) {
        final List<SelectorData> selectorDataList = SELECTOR_MAP.get(data.getPluginName());
        selectorDataList.removeIf(e -> e.getId().equals(data.getId()));
    }
    
    /**
     * Obtain selector data list list.
     *
     * @param pluginName the plugin name
     * @return the list
     */
    public List<SelectorData> obtainSelectorData(final String pluginName) {
        return SELECTOR_MAP.get(pluginName);
    }
    
    /**
     * Cache rule data.
     *
     * @param ruleData the rule data
     */
    public void cacheRuleData(final RuleData ruleData) {
        String selectorId = ruleData.getSelectorId();
        if (RULE_MAP.containsKey(selectorId)) {
            List<RuleData> existList = RULE_MAP.get(selectorId);
            existList.add(ruleData);
            // distinct and sort
            List<RuleData> resultList = existList.stream().sorted(Comparator.comparing(RuleData::getSort))
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(RuleData::getId))), ArrayList::new));
            RULE_MAP.put(selectorId, resultList);
        } else {
            RULE_MAP.put(selectorId, Lists.newArrayList(ruleData));
        }
    }
    
    /**
     * Remove rule data.
     *
     * @param ruleData the rule data
     */
    public void removeRuleData(final RuleData ruleData) {
        final List<RuleData> ruleDataList = RULE_MAP.get(ruleData.getSelectorId());
        ruleDataList.removeIf(rule -> rule.getId().equals(ruleData.getId()));
    }
    
    /**
     * Obtain rule data list list.
     *
     * @param selectorId the selector id
     * @return the list
     */
    public List<RuleData> obtainRuleData(final String selectorId) {
        return RULE_MAP.get(selectorId);
    }
}
