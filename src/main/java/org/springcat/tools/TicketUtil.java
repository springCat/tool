package org.springcat.tools;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @Description TicketUtil
 * @Author springCat
 * @Date 2020/11/3 17:44
 */
public class TicketUtil {

    @Builder
    @Data
    static class Ticket{

        @Builder.Default
        private String month = "06~09";

        @Builder.Default
        private String userName = "陈振威";

        @Builder.Default
        private String accountantId = "8";

        @Builder.Default
        private String path = "C:\\Users\\springcat\\Desktop\\发票";

        private String receiptStation;

        private String receiptNumber;

        private String verificationCode;

        private List<String> receiptDate;

        private String receiptValue;

        private String receiptValueNoTax;

        private String receiptValueTax;

        private File pdfFile;
    }

    public static void main(String[] args) throws IOException {

//        List<String> fileNames = FileUtil.listFileNames(Ticket.$default$path());
        File[] files = FileUtil.ls(Ticket.$default$path());
        List<Ticket> tickets = CollUtil.newArrayList();

        for (File file : files) {
            if(FileUtil.isDirectory(file) || !StrUtil.equalsAnyIgnoreCase("pdf",FileUtil.extName(file))){
                continue;
            }
            try {
                //解析pdf
                Ticket ticket = parseFormPdf(file);
                ticket.setPdfFile(file);
                tickets.add(ticket);
            }catch (Exception e){
                Console.log(ExceptionUtil.stacktraceToOneLineString(e));
            }
        }

        //add to 发票平台
        addReceipt(tickets);

        //生成zip压缩包
        zipPackage(tickets);

        //excel 生成
        toExcel(tickets);

        //合并pdf
        mergePdf(tickets);

    }

    @SneakyThrows
    private static Ticket parseFormPdf(File file){
        // 开始读文字
        PDDocument doc = PDDocument.load(file);

        // 先读出完整的内容
        PDFTextStripper textStripper = new PDFTextStripper();
        textStripper.setSortByPosition(true);

        String allText = textStripper.getText(doc);
        List<String> strings = StrUtil.splitTrim(allText, "\r\n");

        Ticket.TicketBuilder builder = Ticket.builder();
        strings.forEach(line -> {
            if(line.contains("发票代码")){
                Console.log(line);
                Console.log(getSingleNumber(line));
                builder.receiptStation(getSingleNumber(line));
            }

            if(line.contains("发票号码")){
                Console.log(line);
                Console.log(getSingleNumber(line));
                builder.receiptNumber(getSingleNumber(line));
            }

            if(line.contains("校 验 码")){
                line = StrUtil.removeAll(line,' ');
                Console.log(line);
                Console.log(getAllNumber(line));
                builder.verificationCode(StrUtil.subSufByLength(getAllNumber(line).get(1),6));
            }

            if(line.contains("开票日期")){
                Console.log(line);
                Console.log(getAllNumber(line));
                builder.receiptDate(getAllNumber(line));
            }

            if(line.contains("合       计")){
                Console.log(line);
                Console.log(getAllNumber(line));
                List<String> allNumber = getAllNumber(line);
                builder.receiptValueNoTax(allNumber.get(0));
                builder.receiptValueTax(allNumber.get(1));
            }

            if(line.contains("价税合计")){
                Console.log(line);
                Console.log(getSingleNumber(line));
                builder.receiptValue(getSingleNumber(line));
            }

            if(line.contains("价税合计")){
                Console.log(line);
                Console.log(getSingleNumber(line));
                builder.receiptValue(getSingleNumber(line));
            }

        });

        return builder.build();
    }


    private static String getSingleNumber(String line){
        return ReUtil.get("[+-]?\\d+(\\.\\d*)?",line,0);
    }

    private static List<String> getAllNumber(String line){
        return ReUtil.findAll("[+-]?\\d+(\\.\\d*)?",line,0);
    }

    private static void addReceipt(List<Ticket> tickets){
        for (Ticket ticket : tickets) {
            Dict dict = Dict.create();
            dict.put("userName",ticket.getUserName());
            dict.put("receiptStation",ticket.getReceiptStation());
            dict.put("receiptNumber",ticket.getReceiptNumber());
            dict.put("verificationCode",ticket.getVerificationCode());
            dict.put("receiptDate",StrUtil.join("-", ticket.getReceiptDate()));
            dict.put("receiptValue",Convert.toInt(ticket.getReceiptValue()));
            dict.put("accountant.id",ticket.getAccountantId());
            Console.log(dict);
            //String response = HttpUtil.post("http://10.73.155.209:8080/ReceiptSystem/receipt/addReceipt", dict);
            //Console.log(response);
        }
    }

    private static void toExcel(List<Ticket> tickets){
        String excelPath = Ticket.$default$path() + File.separator + "电子发票台账"+ DateUtil.currentSeconds() + ".xlsx";
        ExcelWriter writer = ExcelUtil.getWriter(excelPath);

        List<List<String>> rows = CollUtil.newArrayList();
        List<String> head = CollUtil.newArrayList();
        head.add("*所属公司(全称)");
        head.add("*发票类型(全称)");
        head.add("姓名");
        head.add("*发票代码");
        head.add("*发票号码（8位）");
        head.add("税额");
        head.add("发票日期(yyyy-MM-dd)");
        head.add("不含税金额");
        head.add("校验码后6位");
        head.add("总金额");
        rows.add(head);

        tickets.forEach(ticket ->{
            List<String> row = CollUtil.newArrayList();
            row.add("咪咕数字传媒有限公司");
            row.add("增值税电子发票");
            row.add(ticket.getUserName());
            row.add(ticket.getReceiptStation());
            row.add(ticket.getVerificationCode());
            row.add(ticket.getReceiptValueTax());
            row.add(StrUtil.join("-", ticket.getReceiptDate()));
            row.add(ticket.getReceiptValueNoTax());
            row.add(ticket.getVerificationCode());
            row.add(ticket.getReceiptValue());
            rows.add(row);
        });

        writer.write(rows, true);
        writer.close();
    }

    public static void zipPackage(List<Ticket> tickets){

        HashSet<String> months = CollUtil.newHashSet();
        for (Ticket ticket : tickets) {
            months.add(ticket.receiptDate.get(1));
        }
        months.add("10");
        months.add("01");
        months.add("02");
        months.add("03");
        months.add("04");
        List<String> monthList = CollUtil.newArrayList(months);
        CollUtil.sort(monthList,String::compareTo);


        //姓名_06~09交通费报销发票.zip
        String dirName = StrFormatter.format("{}_{}交通费报销发票",Ticket.$default$userName(),Ticket.$default$month());
        String dirPath = Ticket.$default$path() + File.separator +dirName;

        for (Ticket ticket : tickets) {
            //月份-发票号（8位）-含税金额
            String newFileName = StrFormatter.format("{}-{}-{}.pdf",ticket.getReceiptDate().get(1),ticket.getReceiptNumber(),ticket.getReceiptValue());
            String newPath = dirPath +  File.separator + newFileName;
            File newFile = FileUtil.newFile(newPath);
            FileUtil.move(ticket.getPdfFile(),newFile,false);
        }

        //zip生成
        ZipUtil.zip(dirPath);
        FileUtil.del(FileUtil.mkdir(dirPath));
    }

    @SneakyThrows
    public static void mergePdf(List<Ticket> tickets){
        PDFMergerUtility mergePdf = new PDFMergerUtility();
        for (Ticket ticket : tickets) {
            mergePdf.addSource(ticket.getPdfFile());
        }
        // 设置合并生成pdf文件名称
        String mergePdfPath = Ticket.$default$path() + File.separator + "电子发票合集"+ DateUtil.currentSeconds() + ".pdf";
        mergePdf.setDestinationFileName(mergePdfPath);
        mergePdf.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());


    }
}
