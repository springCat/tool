package org.springcat.tools;

import cn.hutool.core.lang.Console;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReadConfig;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateUtil;
import java.io.StringReader;
import java.util.Map;
/**
 * @Description TestArgs
 * @Author springCat
 * @Date 2020/10/22 18:26
 */
public class TestBlankLine {

    public static void main(String[] args) {

        String s = "uid,age,sex,active_date,limit,data\n" +
                "1,2,3,4,5,\",ds\"\n" +
                "11,2,3,4,5,\",ds\"\n" +
                "111,2,3,4,5,\",ds\"\n" +
                "1111,2,3,4,5,\",ds\"\n" +
                "11111,2,3,4,5,\",ds\"";

        StringReader reader = StrUtil.getReader(s);
        CsvReadConfig config =  CsvReadConfig.defaultConfig();
        config.setContainsHeader(true);
        CsvReader csvReader = new CsvReader(reader,config);

        CsvData read = csvReader.read();
        Console.log(read.getHeader());


//        String template = "1${uid},2${age}efwefwe3${sex},4${active_date},5${limit},6${data}\n";
        String template = "<#list 1..100 as i>\n" +
                "\t<td>${i}</td>\n" +
                "</#list>";
        Template template1 = TemplateUtil.createEngine().getTemplate(template);

        read.forEach(row -> {
            Map<String, String> fieldMap = row.getFieldMap();
            String render = template1.render(fieldMap);
            Console.log(render);
        });

    }
}
