package com.casper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *
 * Provide console functionality for capser library
 */
public class Console {

    public static void printUsage() {

        //need to be localized
        System.out.println("Usage :");
        System.out.println("\t > inputfilename                            ;return evaluated output");
        System.out.println("\t > -compile inputfilename                   ;return javascript code");
        System.out.println("\t > inputfilename outputfilename             ;return evaluated output to outputfile");
        System.out.println("\t > -compile inputfilename outputfilename    ;return javascript code to outputfile");

    }

    public static void main(String arg[]) {
          try {
            switch (arg.length) {
                case 1:
                    File ip0 = new File(arg[0]);
                    Casper.eval(new FileInputStream(ip0), System.out);
                    break;
                case 2:
                    if (arg[0].equals("-compile")) {
                        File ip1 = new File(arg[1]);
                        Casper.compile(new FileInputStream(ip1), System.out);
                    }else{
                    File ip2 = new File(arg[0]);
                    File ip3 = new File(arg[1]);
                    Casper.eval(new FileInputStream(ip2), new FileOutputStream(ip3));
                    }
                    break;
                case 3:
                    if (arg[0].equals("-compile")) {
                        File ip4 = new File(arg[1]);
                        File ip5 = new File(arg[2]);
                        Casper.compile(new FileInputStream(ip4), new FileOutputStream(ip5));
                    }
                    break;
                default:
                    printUsage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
