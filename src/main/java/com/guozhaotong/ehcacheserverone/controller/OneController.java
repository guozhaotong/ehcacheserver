package com.guozhaotong.ehcacheserverone.controller;

import com.guozhaotong.ehcacheserverone.util.CsvUtil;
import com.guozhaotong.ehcacheserverone.util.EhCacheUtil;
import net.sf.ehcache.distribution.RMICacheManagerPeerListenerFactory;
import net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OneController {

    @PostMapping("/put")
    public String putIntoEhcache(String key, String value) {
//        EhCacheUtil.getInstance().put("ehcache001", key, value);
        CsvUtil csvUtil = new CsvUtil();
        List<List<Object>> list = csvUtil.read("/home/tong/文档/测试用例/table2w.csv");
        EhCacheUtil.getInstance().put("ehcache001", "list", list);
        System.out.println(System.currentTimeMillis());
        return "put " + key + " done";
    }

    @GetMapping("/get")
    public String get(String key) {
        return EhCacheUtil.getInstance().get("ehcache001", key);
    }

    @DeleteMapping("/remove")
    public void remove(String key) {
        EhCacheUtil.getInstance().remove("ehcache001", key);
    }

    public static void main(String[] args) {
        RMICacheManagerPeerProviderFactory rmiCacheManagerPeerProviderFactory = new RMICacheManagerPeerProviderFactory();
//        rmiCacheManagerPeerProviderFactory.
        RMICacheManagerPeerListenerFactory rmiCacheManagerPeerListenerFactory;
    }
}
