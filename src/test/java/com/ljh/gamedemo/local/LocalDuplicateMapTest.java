package com.ljh.gamedemo.local;

import com.ljh.gamedemo.module.duplicate.local.LocalBossMap;
import com.ljh.gamedemo.module.duplicate.local.LocalDuplicateMap;
import com.ljh.gamedemo.module.equip.local.LocalEquipMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: Heiku
 * @Date: 2019/7/29
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class LocalDuplicateMapTest {

    @Test
    public void readExcel() throws Exception{
        LocalBossMap.readExcel();
        LocalEquipMap.readExcel();

        LocalDuplicateMap.readExcel();

        LocalDuplicateMap.getDuplicateMap().forEach((k, v) -> {
            System.out.println("k: " + k  + " v: " + v);
        });
    }
}