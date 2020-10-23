package org.springcat.tools;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.cli.ParseException;

import java.util.List;

/**
 * @Description Api2Proto
 * @Author springCat
 * @Date 2020/10/22 14:12
 */
public class Api2Json {

    public static void main(String[] args) throws ParseException {


        String request = ClipboardUtil.getStr();

        Api2Json api2PbGen = new Api2Json();
        api2PbGen.handle(request);
    }

    private void handle(String request) {

        request = StrUtil.replace(request,"\t"," ");

        //request
        List<String> requestRows = StrUtil.splitTrim(request, '\n');

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");
        for (int i = 0; i < requestRows.size(); i++) {
            String row = StrUtil.trim(requestRows.get(i));
            if (StrUtil.isBlank(row)) {
                continue;
            }
            List<String> columns = StrUtil.splitTrim(row, ' ');
            String name = CollectionUtil.get(columns, 0);
            String must = CollectionUtil.get(columns, 1);
            String parent = CollectionUtil.get(columns, 2);
            String type = CollectionUtil.get(columns, 3);
            String desc = CollectionUtil.get(columns, 4);

            String value;
            boolean isString = StrUtil.equalsAnyIgnoreCase("string", type);
            if(isString){
                value = "\"\"";
            }else {
                value = "";
            }

            if(i < requestRows.size()-1){
                value += ",";
            }

            String jsonColumn = String.format("\"%s\":%s \n", name,value);
            String jsonDescColumn = String.format("//类型:%s | 描述:%s | 是否必须:%s \n", type,StrUtil.nullToEmpty(desc),must);

            stringBuilder.append(jsonColumn);
            stringBuilder.append(jsonDescColumn);
        }
        stringBuilder.append("}\n");

        System.out.println(stringBuilder.toString());
        ClipboardUtil.setStr(stringBuilder.toString());
    }

}
