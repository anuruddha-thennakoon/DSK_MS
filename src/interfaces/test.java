/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

/**
 *
 * @author ANURUDDHA
 */
public class test {

    static private String c;

    public static void main(String[] args) {

        try {
            System.out.println("in the catch");
            int i=10/0;
            c="Amal";
            System.out.println(c);
            c = "Amalasd";
            System.out.println(c);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out.println("finally");
        }
    }
}
