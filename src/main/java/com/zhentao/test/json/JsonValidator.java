package com.zhentao.test.json;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class JsonValidator {


    public static void main(String args[]) throws InterruptedException {
        Integer a = null;
        String x = "a" + a;
        System.out.println(x);

        for (String str : "".split("")) {
            System.out.println("abc" + str + "def");
        }
        Set<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            set.add(i);
            TimeUnit.MILLISECONDS.sleep(10);
        }
        while (true) {
            TimeUnit.SECONDS.sleep(5);
        }
//        try {
//            System.out.println("start");
//            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream("/Users/liz/Downloads/discover"), "UTF-8"));
//            reader.beginObject();
//            while(reader.hasNext()) {
//                reader.nextName();
//                reader.skipValue();
//            }
//            reader.endObject();
//            reader.close();
//            System.out.print("done");
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
    }
}
