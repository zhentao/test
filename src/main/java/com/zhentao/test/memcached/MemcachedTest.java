package com.zhentao.test.memcached;

import java.net.SocketAddress;
import java.util.Map;

import net.spy.memcached.MemcachedClient;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MemcachedTest {

    public static void main(String args[]) throws Exception {
        //MemcachedClient client = new MemcachedClient(new BinaryConnectionFactory(),
        //        AddrUtil.getAddresses("localhost:11211 localhost:11212"));
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        MemcachedClient client = context.getBean(MemcachedClient.class);
        Map<SocketAddress, Map<String, String>> map = client.getStats();


//        client.set("key1", 0, "value1" );
//        client.set("key2", 0, "value2");

            try {
                System.out.println("Value 1: " + client.get("key1"));
            } catch (Exception e) {
                System.out.println("Exception when getting value 1: " + e.getMessage());
                e.printStackTrace();
            }
            try {
                System.out.println("Value 2: " + client.get("key2"));
            } catch (Exception e) {
                System.out.println("Exception when getting value 2: " + e.getMessage());
                e.printStackTrace();
            }
            Thread.sleep(1000);
            System.out.println("Unavailable servers: " + client.getUnavailableServers());

//            client.set("key1", 0, "value1" );

        System.out.println("after recover");
//        client.set("key1", 0, "value1" );
        //client.set("key2", 0, "value2");

        System.out.println("Value 1: " + client.get("key1"));
        System.out.println("Value 2: " + client.get("key2"));
        context.close();
        System.exit(0);
    }
}
