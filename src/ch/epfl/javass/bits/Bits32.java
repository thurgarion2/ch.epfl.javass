package ch.epfl.javass.bits;
import ch.epfl.javass.Preconditions;


public class Bits32 {
    private Bits32(){}

    public static void main(String[] args) {
        int x=-780879113;
        System.out.println(x);

    }

    private static void checkSize(int bits, int size){
        Preconditions.checkArgument(size>0 && size<Integer.SIZE);
        int mask=mask(size,Integer.SIZE-size);
        Preconditions.checkArgument((bits&mask)==0);
    }

    public static int mask(int start, int size){
        Preconditions.checkArgument(start>=0 && start<=Integer.SIZE);
        Preconditions.checkArgument(start+size<=Integer.SIZE  && size>=0);
        if(size==32) {
            return (mask(0,31)<<1 | 0b1)<<start;
        }
        int mask = (1<<size)-1;
        return mask<<start;
    }

    public static int extract(int bits, int start, int size){
        Preconditions.checkArgument(start>=0 && start<=Integer.SIZE);
        Preconditions.checkArgument(start+size<=Integer.SIZE  && size>=0);
        int fromStart= bits >>> start;
        int fromSize= bits >>> (start+size);
        
        if(size>=31) {
            fromSize=bits >>> 30;
            fromSize=fromSize>>> (size-30);
        }
     
        return fromStart^(fromSize<<size);

    }

    public static int pack(int v1, int s1, int v2, int s2){
        checkSize(v1, s1);
        checkSize(v2, s2);
        Preconditions.checkArgument(s1+s2<=Integer.SIZE);
        return v1|v2<<s1;
    }

    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3){
        Preconditions.checkArgument(s1+s2+s3<=Integer.SIZE);
        int v1ToV2 =pack( v1,  s1, v2,  s2);
        return pack( v1ToV2,  s1+s2, v3,  s3);
    }

    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3, int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7){
        Preconditions.checkArgument(s1+s2+s3+s4+s5+s6+s7<=Integer.SIZE);

        int v1ToV3 = pack( v1,  s1, v2,  s2, v3, s3);
        int v4ToV6 = pack( v4,  s4, v5,  s5, v6, s6);
        int v1ToV7 = pack(v1ToV3, s1+s2+s3, v4ToV6, s4+s5+s6,v7,s7);
        return v1ToV7;
    }

}
