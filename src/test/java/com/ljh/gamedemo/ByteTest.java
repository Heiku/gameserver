package com.ljh.gamedemo;

import com.ljh.gamedemo.entity.Creep;

import java.util.ArrayList;
import java.util.List;

public class ByteTest {

    public static void main(String[] args) throws Exception{
        //System.out.println(0x10);

        /*Creep c1 = new Creep();
        c1.setCreepId(1);
        c1.setName("小");

        Creep c2 = new Creep();
        c2.setCreepId(2);
        c2.setName("大");

        List<Creep> creeps = new ArrayList<>();

        //creeps.add(c2);
        creeps.add(c1);
        creeps.sort((a, b) -> a.getCreepId().compareTo(b.getCreepId()));

        System.out.println(creeps);*/


        long start = System.currentTimeMillis();

        Thread.sleep(6 * 1000);

        long end = System.currentTimeMillis();

        int cd = 4;

        System.out.println(Math.floor(((end - start) - cd * 1000)/ 1000));
    }
}
