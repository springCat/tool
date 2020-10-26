package org.springcat.tools;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReadConfig;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateUtil;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * @Description TemplateFormat
 * @Author springCat
 * @Date 2020/10/26 15:35
 */
public class TemplateFormat {

    /**
     *      String template = "1${uid},2${age}efwefwe3${sex},4${active_date},5${limit},6${data}\n";
     *
     *      String data = "uid,age,sex,active_date,limit,data\n" +
     *                 "1,2,3,4,5,\",ds\"\n" +
     *                 "11,2,3,4,5,\",ds\"\n" +
     *                 "111,2,3,4,5,\",ds\"\n" +
     *                 "1111,2,3,4,5,\",ds\"\n" +
     *                 "11111,2,3,4,5,\",ds\"";
     * @param args
     */
    public static void main(String[] args) {

        String str = ClipboardUtil.getStr();
        List<String> parts = StrUtil.splitTrim(str, "\n\n");

        String template = CollectionUtil.get(parts,0);
        String csvData = CollectionUtil.get(parts,1);
        if(StrUtil.isBlank(template)){
            return;
        }

        //init csv data
        StringReader templateReader = StrUtil.getReader(csvData);
        CsvReadConfig config =  CsvReadConfig.defaultConfig();
        config.setSkipEmptyRows(true);
        config.setContainsHeader(true);
        CsvReader csvReader = new CsvReader(templateReader,config);
        CsvData read = csvReader.read();

        //init template
        Template templateFreemarker = TemplateUtil.createEngine().getTemplate(template);

        StringBuilder stringBuilder = new StringBuilder();
        //format
        read.forEach(row -> {
            Map<String, String> fieldMap = row.getFieldMap();
            String render = templateFreemarker.render(fieldMap);

            stringBuilder.append(render)
                    .append("\n")
                    .append("\n");

        });

        Console.log(stringBuilder);
        ClipboardUtil.setStr(stringBuilder.toString());
    }
}
