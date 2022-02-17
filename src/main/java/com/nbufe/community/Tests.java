package com.nbufe.community;

public class Tests {
    public boolean chkRotation(String A, int lena, String B, int lenb) {
        // write code here
        if(lena!=lenb){
            return false;
        }
        String C=A+A;
        for(int i=0;i<C.length()-lena;i++){
            if(C.substring(i,i+lena).equals(B)){
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) {
        Tests tests=new Tests();
        System.out.println(tests.chkRotation("cdab",4,"abcd",4));

    }


}
