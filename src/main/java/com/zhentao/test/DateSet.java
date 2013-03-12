package com.zhentao.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateSet {
    public static void main(String[] args) {
        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");

        Calendar c = Calendar.getInstance();
        System.out.println(f.format(c.getTime()));

        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        System.out.println(f.format(c.getTime()).toUpperCase());

        c.add(Calendar.DAY_OF_MONTH, 1);
        System.out.println(f.format(c.getTime()).toUpperCase());

        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        System.out.println(f.format(c.getTime()).toUpperCase());

        Abc abc =  null;
        if (abc == Abc.ONE) {
            System.out.println("test");
        }
    }

    enum Abc {
        ONE;
    }
}
