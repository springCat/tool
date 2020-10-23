package org.springcat.tools;

import org.apache.commons.cli.*;

/**
 * @Description TestArgs
 * @Author springCat
 * @Date 2020/10/22 18:26
 */
public class TestArgs {
    public static void main(String[] args) throws ParseException {

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        // 使用 $ java -jar App.jar -h
        options.addOption("h","help",false,"help info");
        // $java -jar App.jar --file test.txt
        options.addOption("f","file",true,"file output");

        CommandLine commandLine = parser.parse(options,args);

        if (commandLine.hasOption("h")){
            System.out.println("Help Message");
            System.exit(0);
        }

        if (commandLine.hasOption("f")){
            System.out.println(commandLine.getOptionValue("f"));
        }

    }
}
