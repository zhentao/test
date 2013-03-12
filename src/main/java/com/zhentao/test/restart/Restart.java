package com.zhentao.test.restart;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

public class Restart {

    public static void main(String[] args) throws IOException {
        System.out.println("Test restart the application!");
        restart();
    }

    private static void restart() throws IOException {
        RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
        List<String> jvmArgs = mx.getInputArguments();
        String cp = mx.getClassPath();

        List<String> listArgs = new ArrayList<String>();
        listArgs.add("java");

        listArgs.addAll(jvmArgs);
        listArgs.add("-cp");
        listArgs.add(cp);
        listArgs.add(Restart.class.getName());

        ProcessBuilder pb = new ProcessBuilder(listArgs);
        pb.start();

        //System.out.println(listArgs.toString());
        System.exit(0);
    }
}
