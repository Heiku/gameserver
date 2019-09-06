package com.ljh.gamedemo.local;

import com.ljh.gamedemo.module.equip.local.LocalEquipMap;
import com.ljh.gamedemo.module.items.local.LocalItemsMap;
import com.ljh.gamedemo.module.mall.local.LocalCommodityMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
        System.out.println(LocalCommodityMap.getEquipsList(10002L));

    }
}