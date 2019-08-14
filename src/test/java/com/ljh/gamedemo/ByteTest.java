package com.ljh.gamedemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Random;
import java.util.UUID;

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

        /*long[] l = new long[]{10001,10002,10003,10004,10005,10006};
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < l.length; j++){
                System.out.print(l[j] % 10 + " ");
            }
            System.out.println();
        }*/

        /*No no = new No(2, "nono");
        List<No> noList = new ArrayList<>();
        noList.add(no);

        no = noList.get(0);
        no.setName("yesyes");

        System.out.println(noList);
*/


        /*LocalDateTime s = LocalDateTime.now();
        long st = s.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();

        Thread.sleep(3000);

        LocalDateTime e = LocalDateTime.now();
        long et = s.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();

        System.out.println(et - st);*/

        No n1 = new No(1,"nono");
        No n2 = new No(2,"yesyes");
       /* List<No> noList = new ArrayList<>();
        noList.add(n1);
        noList.add(n2);

        No n3 = n1;
        noList.remove(n3);

        for (No no : noList) {
            System.out.println(no);
        }*/
        /*System.out.println(n1.hashCode());
        n1.setName("nonono");
        System.out.println(n1.hashCode());*/


        //System.out.println( (int) (0.9 * 10));


     /*   int[] a = new int[]{1,2};
        int[] b = a;

        a[0] = 2;
        for (int i : b) {
            System.out.println(i);
        }*/


        System.out.println(Math.abs(new Random().nextLong()));
    }
}

@Data
@AllArgsConstructor
class No{

    private Integer id;

    @EqualsAndHashCode.Exclude
    private String name;

}
