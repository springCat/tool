package org.springcat.tools;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.cli.*;
import java.io.File;
import java.util.List;

/**
 * @Description Api2Proto
 * @Author springCat
 * @Date 2020/10/22 14:12
 */
public class Api2Proto {

    private String apiName = "getDiscoverTabTop";

    private String request = "tabId\tY\tRequest\tlong\tTAB ID";

    private String response = "discoverTabTopList\t1\tresponse\tList\t\n" +
            "discoverTabTop\t0..N\tdiscoverTabTopList\tDiscoverTabTop\t\n" +
            "contentId\t1\tdiscoverTabTop\tlong\t内容id\n" +
            "contentType\t1\tdiscoverTabTop\tstring\t内容类型\n" +
            "showTopSign\t1\tdiscoverTabTop\tint\t是否显示置顶表示 0 不显示，1显示\n" +
            "setTopNo\t1\tdiscoverTabTop\tint\t置顶序号\n";

    public static void main(String[] args) throws ParseException {


        String str = ClipboardUtil.getStr();
        String[] parts = StrUtil.split(str, "\n\n");


        String apiName = ArrayUtil.get(parts,0);
        String request = ArrayUtil.get(parts,1);
        String response = ArrayUtil.get(parts,2);

        if(StrUtil.isBlank(apiName) || StrUtil.isBlank(request) || StrUtil.isBlank(response)){
            return;
        }

        Api2Proto api2PbGen = new Api2Proto();
        api2PbGen.handle(apiName, request, response);
    }

    private void handle(String apiName, String request, String response) {

        //初始化类型映射
        Dict typeDict = new Dict();
        typeDict.put("int", "int32");
        typeDict.put("long", "int64");
        typeDict.put("string", "string");
        //添加头部
        println("syntax = \"proto3\";");
        println("option java_package = \"cn.migu.sns.interact.api\";");
        println("option java_multiple_files = true;");

        //request
        println("");
        println(String.format("message %sRequest{", StrUtil.upperFirst(apiName)));
        List<String> requestRows = StrUtil.split(request, '\n');
        for (int i = 0; i < requestRows.size(); i++) {
            String row = StrUtil.trim(requestRows.get(i));
            if (StrUtil.isBlank(row)) {
                continue;
            }
            List<String> columns = StrUtil.split(row, '\t');
            String name = CollectionUtil.get(columns, 0);
            String must = CollectionUtil.get(columns, 1);
            String parent = CollectionUtil.get(columns, 2);
            String type = CollectionUtil.get(columns, 3);
            String desc = CollectionUtil.get(columns, 4);
            println("");
            println(String.format("\t // %s", StrUtil.nullToEmpty(desc)));
            println(String.format("\t // 是否必传：%s", must));
            println(String.format("\t %s %s = %d;", typeDict.get(type), name, i + 1));
        }
        println("}");

        //response
        println("");
        println(String.format("message %sResponse{", StrUtil.upperFirst(apiName)));
        List<String> responseRows = StrUtil.split(response, '\n');
        for (int i = 0; i < responseRows.size(); i++) {
            String row = StrUtil.trim(responseRows.get(i));
            if (StrUtil.isBlank(row)) {
                continue;
            }
            List<String> columns = StrUtil.split(row, '\t');
            String name = CollectionUtil.get(columns, 0);
            String must = CollectionUtil.get(columns, 1);
            String parent = CollectionUtil.get(columns, 2);
            String type = CollectionUtil.get(columns, 3);
            String desc = CollectionUtil.get(columns, 4);

            println("");
            println(String.format("\t // %s", StrUtil.nullToEmpty(desc)));
            println(String.format("\t // 是否必传：%s", must));
            println(String.format("\t %s %s = %d;", typeDict.get(type), name, i + 1));
        }
        println("}");
        toFile(apiName + ".proto");
        ClipboardUtil.setStr(stringBuilder.toString());
    }


    private StringBuilder stringBuilder = new StringBuilder();

    private void println(String line) {
        stringBuilder.append(line);
        stringBuilder.append("\n");
    }

    private void toFile(String fileName) {

        FileUtil.writeString(stringBuilder.toString(), FileUtil.getUserHomePath() + File.separator + "Desktop" + File.separator + fileName, "UTF-8");
    }


}
