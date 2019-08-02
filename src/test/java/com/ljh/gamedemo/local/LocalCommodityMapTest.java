package com.ljh.gamedemo.local;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Author: Heiku
 * @Date: 2019/8/2
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class LocalCommodityMapTest {

    @Test
    public void main() throws Exception {
        LocalItemsMap.readExcel();
        LocalEquipMap.readExcel();
        LocalCommodityMap.readExcel();

        System.out.println(LocalCommodityMap.getItemsList());
        System.out.println(LocalCommodityMap.getEquipsList());

    }
}