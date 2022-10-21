package org.galatea.starter.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class HistoricalPricesCache {
    private String cacheFilePath;

    private Map<String, Map<String, List<IexHistoricalPrice>>> rangedCache;
    private Map<String, Map<LocalDate, List<IexHistoricalPrice>>> datedCache;

    public HistoricalPricesCache(String cacheFilePath) {
        this.cacheFilePath = cacheFilePath;

        log.info("Attempting to load historical prices cache from disk. Loading from {}", cacheFilePath);

        try(FileInputStream fi = new FileInputStream(new File(cacheFilePath))) {
            log.info("Cache file found. Initialising cache...");

            try (ObjectInputStream oi = new ObjectInputStream(fi)) {
                rangedCache = (Map<String, Map<String, List<IexHistoricalPrice>>>) oi.readObject();
                datedCache = (Map<String, Map<LocalDate, List<IexHistoricalPrice>>>) oi.readObject();

                log.info("Cache loaded. Loaded {} ranged symbols and {} dated symbols", rangedCache.size(), datedCache.size());
            }
            catch (ClassNotFoundException ex) {
                log.warn("Exception raised when loading from cache:\n{}\nInitialising empty cache.", ex);
                rangedCache = new HashMap<>();
                datedCache = new HashMap<>();
            }

        }
        catch (FileNotFoundException ex) {
            log.info("No cache file at {} found. Initialising empty cache.", cacheFilePath);
            rangedCache = new HashMap<>();
            datedCache = new HashMap<>();
        }
        catch (IOException ex) {
            log.warn("IOException when attempting to load cache file {}\n{}", cacheFilePath, ex);
            log.warn("Cache could not be loaded. Initialising empty cache.", cacheFilePath);
            rangedCache = new HashMap<>();
            datedCache = new HashMap<>();
        }
    }

    /**
     * Retrieve item from ranged cache, returning null if no such item exists
     * Also instantiates a new second level map if this symbol has not been added yet
     */
    public List<IexHistoricalPrice> checkRangedCache(String symbol, String range) {
        if (rangedCache.containsKey(symbol)) {
            if (rangedCache.get(symbol).containsKey(range)) {
                return rangedCache.get(symbol).get(range);
            }
        }
        else {
            rangedCache.put(symbol, new HashMap<>());
        }

        return null;
    }

    /**
     * Add item to ranged cache
     */
    public void cacheRanged(String symbol, String range, List<IexHistoricalPrice> result) {
        rangedCache.get(symbol).put(range, result);
    }

    /**
     * Retrieve item from dated cache, returning null if no such item exists
     * Also instantiates a new second level map if this symbol has not been added yet
     */
    public List<IexHistoricalPrice> checkDatedCache(String symbol, LocalDate date) {
        if (datedCache.containsKey(symbol)) {
            if (datedCache.get(symbol).containsKey(date)) {
                return datedCache.get(symbol).get(date);
            }
        }
        else {
            datedCache.put(symbol, new HashMap<>());
        }

        return null;
    }

    /**
     * Add item to dated cache
     */
    public void cacheDated(String symbol, LocalDate date, List<IexHistoricalPrice> result) {
        datedCache.get(symbol).put(date, result);
    }

    /**
     * Save contents of cache back to disk. Should be called when the application shuts down
     */
    public void save() {
        log.info("Saving historical prices cache to disk. Saving too {}", cacheFilePath);

        try(ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(new File(cacheFilePath)))) {
            o.writeObject(rangedCache);
            o.writeObject(datedCache);
        }
        catch (FileNotFoundException ex) {
            log.warn("Exception raised when saving to cache file at {}:\n{}", cacheFilePath, ex);
        } catch (IOException ex) {
            log.warn("Exception raised when saving to cache file at {}:\n{}", cacheFilePath, ex);
        }
    }
}
