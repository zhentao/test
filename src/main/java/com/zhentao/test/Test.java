package com.zhentao.test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
    private static final Logger LOG = LoggerFactory.getLogger(Test.class);

    public static void put() throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPut put = new HttpPut("http://localhost:8080/feeds/config/postech");
        StringEntity entity = new StringEntity(
                "{\"download_type_id\": 2, \"fr_user_name\": null, \"fr_password\": null, \"feed_name\": \"Position Technologies\",\"feed_code\": \"postech\",\"download_frequency_id\": 2,\"convert_type_id\": 1,\"is_confirming\": true,\"compression_id\": 0,\"trusted_feed\": false,\"review_display_option_id\": 1,\"is_aggregate_feed\": false,\"attribution_text\": \"Position Technologies\",\"attribution_url\": null,\"attribution_logo_url\": null}",
                "application/json", "utf-8");
        put.setEntity(entity);
        HttpResponse response = httpclient.execute(put);
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
            InputStream instream = responseEntity.getContent();
            try {
                // do something useful
            } finally {
                instream.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String[] aKvPair = "".split("=", 2);

        System.out.println(aKvPair.length);


    }
}

class Node {
    Integer value;
    Node next;

    public Node(Integer value, Node next) {
        this.value = value;
        this.next = next;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public void print() {
        System.out.print(value + " ");
        if (next != null) {
            next.print();
        }
    }
}

class Util {
    public static void reverseNode(Node node) {
        Node previous = null;
        while (node != null) {
            Node next = node.next;
            node.next = previous;
            previous = node;
            node = next;
        }
    }
}
