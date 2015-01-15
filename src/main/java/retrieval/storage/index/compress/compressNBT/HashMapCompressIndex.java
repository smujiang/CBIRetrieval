/*
 * Copyright 2009-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrieval.storage.index.compress.compressNBT;

import java.util.HashMap;
import java.util.Map;
import retrieval.config.ConfigServer;

/**
 * Compress index for memory db
 * @author Loïc Rollus
 */
public class HashMapCompressIndex extends CompressIndexNBT {
    Map<String,Integer> blacklistedVW;

    public HashMapCompressIndex(ConfigServer config) {
        super(config.getIndexCompressThreshold());
        blacklistedVW = new HashMap<String,Integer>();
    }

    /**
     * Blacklist a visualword
     * @param b visualword
     */    
    public void blacklistVW(String b) {
        blacklistedVW.put(b, 1);
    }

    /**
     * Retrieve all blacklisted VW
     */    
    public Map<String,Integer> getBlacklistedVW() {
        return blacklistedVW;
    }

   /**
     * Check if a visualword is blacklisted in the storage for this test vector
     * @param b visualword
     * @return true if b is blacklisted
     */    
    public boolean isBlackListed(String b) {
        return blacklistedVW.containsKey(b);
    }


}