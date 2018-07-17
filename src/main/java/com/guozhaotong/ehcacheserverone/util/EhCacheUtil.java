package com.guozhaotong.ehcacheserverone.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.*;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class EhCacheUtil {

    private CacheManager manager;

    private static EhCacheUtil ehCache;

    private EhCacheUtil() {
        CacheConfiguration ehcache001Conf = new CacheConfiguration()
                .name("ehcache001")
//                .overflowToOffHeap(false)
                .overflowToDisk(false)
                .maxBytesLocalHeap(1024, MemoryUnit.MEGABYTES)
                .eternal(false)
                .timeToLiveSeconds(86400)
                .timeToIdleSeconds(86400)
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
                .transactionalMode("off");

        Configuration config = new Configuration();

        FactoryConfiguration factoryConfigforpeerprovider = new FactoryConfiguration();
        factoryConfigforpeerprovider.setClass("net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory");
//        factoryConfigforpeerprovider.setProperties("peerDiscovery=manual,rmiUrls=//127.0.0.1:40002/ehcache001");
        factoryConfigforpeerprovider.setProperties("peerDiscovery=automatic, multicastGroupAddress=230.0.0.1,multicastGroupPort=4446, timeToLive=32");
        config.addCacheManagerPeerProviderFactory(factoryConfigforpeerprovider); // Configuration for PeerProvider

        FactoryConfiguration factoryConfigforpeerlistener = new FactoryConfiguration();
        factoryConfigforpeerlistener.setClass("net.sf.ehcache.distribution.RMICacheManagerPeerListenerFactory");
        factoryConfigforpeerlistener.setProperties("hostName=127.0.0.1,port=40001,socketTimeoutMillis=6000");
        config.addCacheManagerPeerListenerFactory(factoryConfigforpeerlistener);

        CacheConfiguration.CacheEventListenerFactoryConfiguration cacheEventListenerFactoryConfiguration = new CacheConfiguration.CacheEventListenerFactoryConfiguration();
        cacheEventListenerFactoryConfiguration.setClass("net.sf.ehcache.distribution.RMICacheReplicatorFactory");
        cacheEventListenerFactoryConfiguration.setProperties("replicateAsynchronously=false,replicatePuts=true,replicateUpdates=true," +
                "replicateUpdatesViaCopy=false,replicateRemovals=true");
        ehcache001Conf.addCacheEventListenerFactory(cacheEventListenerFactoryConfiguration);

        CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration bootstrapCacheLoaderFactoryConfiguration = new CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration();
        bootstrapCacheLoaderFactoryConfiguration.setClass("net.sf.ehcache.distribution.RMIBootstrapCacheLoaderFactory");
        ehcache001Conf.addBootstrapCacheLoaderFactory(bootstrapCacheLoaderFactoryConfiguration);

        SizeOfPolicyConfiguration sizeOfPolicyConfiguration = new SizeOfPolicyConfiguration();
        sizeOfPolicyConfiguration.setMaxDepth(100000);
        sizeOfPolicyConfiguration.maxDepthExceededBehavior(SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT);
        ehcache001Conf.addSizeOfPolicy(sizeOfPolicyConfiguration);

        manager = new CacheManager(config);
        Cache ehcache001 = new Cache(ehcache001Conf);
        manager.addCache(ehcache001);
    }

    public static EhCacheUtil getInstance() {
        if (ehCache == null) {
            ehCache = new EhCacheUtil();
        }
        return ehCache;
    }

    public void put(String cacheName, String key, Object value) {
        Cache cache = manager.getCache(cacheName);
        Element element = new Element(key, value);
        cache.put(element);
    }

    public String get(String cacheName, String key) {
        Cache cache = manager.getCache(cacheName);
        if (cache == null) {
            return null;
        }
        Element element = cache.get(key);
        return element == null ? null : element.getObjectValue().toString();
    }

    public Cache getCache(String cacheName) {
        return manager.getCache(cacheName);
    }

    public void remove(String cacheName, String key) {
        Cache cache = manager.getCache(cacheName);
        cache.remove(key);
    }

    public static void main(String[] args) {
        //string测试
        EhCacheUtil.getInstance().put("ehcache001","userEch","test001");
        String val = (String) EhCacheUtil.getInstance().get("ehcache001", "userEch");
        System.out.println(val);

    }
}
