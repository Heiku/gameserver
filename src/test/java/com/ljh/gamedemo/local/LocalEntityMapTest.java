package com.ljh.gamedemo.local;

import com.ljh.gamedemo.entity.Role;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LocalEntityMapTest {

    @Test
    public void testReadPlayer(){
        /*LocalEntityMap.readPlayerCsv();

        for (Map.Entry<Long, Role> entry : LocalEntityMap.playerMap.entrySet()){
            System.out.println("key= " + entry.getKey() + "  value= " + entry.getValue());
        }*/
    }
}