package com.ljh.gamedemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

/*
        Duplicate d1 = new Duplicate();
        d1.setId(1L);
        d1.setName("nono");

        Duplicate d2 = new Duplicate();
        d2.setId(1L);
        d2.setName("nono");

        System.out.println(d1.hashCode());
        System.out.println(d2.hashCode());
        System.out.println(d1.equals(d2));*/


        /*DateTime now = new DateTime();
        System.out.println(now.toDate().getTime());

        DateTime later = now.plus(Minutes.minutes(2)).toDateTime();
        System.out.println(later.toDate().getTime());*/

        //System.out.println(0.3 - 0.2 == 0.1);


        int a1 = 100;
        Integer a2 = new Integer(100);
        Integer a3 = 100;
        Integer a4 = new Integer(100);
        System.out.println(a2 == a3);
        System.out.println(a2.equals(a3));
        System.out.println(a2 == a4);
        System.out.println(a2.equals(a4));
        System.out.println(a1 == a2);

    }
}

@Data
@AllArgsConstructor
class No{

    private Integer id;

    @EqualsAndHashCode.Exclude
    private String name;

}
