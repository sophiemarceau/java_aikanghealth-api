package com.example.his.api.report;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import lombok.SneakyThrows;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CheckupReportUtil {
    public void createCover(XWPFDocument doc, HashMap param) throws Exception {
        //创建顶部体检中心中文全称
        XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setFontSize(20);
        run.setFontFamily("Microsoft YaHei"); //使用雅黑字体
        run.setUnderline(UnderlinePatterns.THICK); //带有下划线
        run.setText("耀东大健康体检系统");
        CTP ctp = paragraph.getCTP();
        CTPPr ctpPr = ctp.addNewPPr();
        CTSpacing ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setLineRule(STLineSpacingRule.EXACT);
        ctSpacing.setLine(BigInteger.valueOf(480));
        //创建顶部体检中心英文全称
        paragraph = doc.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(10);
        run.setFontFamily("Microsoft YaHei");//使用雅黑字体
        run.setText("Beijing YaoDong Grand Health Examination Center");
        //设置行间距
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setLineRule(STLineSpacingRule.EXACT);
        ctSpacing.setLine(BigInteger.valueOf(280));

        //创建体检报告标题
        paragraph = doc.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setBold(true);
        run.setFontSize(26);
        run.setFontFamily("Microsoft YaHei");
        run.setText("体检报告书");
        //设置段落之间的间距（类似CSS的上下外填充）
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing = ctpPr.addNewSpacing();
        //上下段落间距较大，腾出空白空间
        ctSpacing.setBeforeLines(BigInteger.valueOf(800));
        ctSpacing.setAfterLines(BigInteger.valueOf(800));

        //插入体检2维码图片
        QrConfig qrConfig = new QrConfig();
        qrConfig.setWidth(150);
        qrConfig.setHeight(150);
        qrConfig.setMargin(2);
        String uuid = MapUtil.getStr(param, "uuid");
        //生成二维码图片对象
        BufferedImage bufferedImage = QrCodeUtil.generate(uuid, qrConfig);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        //把二维码图片对象的内容写到输出流中
        ImageIO.write(bufferedImage, "jpg", bout);
        InputStream input = new ByteArrayInputStream(bout.toByteArray());
        //创建新的段落
        paragraph = doc.createParagraph();
        //图片居中对齐
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        //设置图片下外填充
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setAfterLines(BigInteger.valueOf(400));

        run = paragraph.createRun();
        //往段落中插入图片
        run.addPicture(input, Document.PICTURE_TYPE_JPEG, "",
                Units.pixelToEMU(bufferedImage.getWidth()),
                Units.pixelToEMU(bufferedImage.getHeight()));
        /**
         * 插入表格
         */
        XWPFTable table = doc.createTable(4, 2);
        //表格处于水平居中
        table.setTableAlignment(TableRowAlign.CENTER);
        //表格没有边框
        table.getCTTbl().getTblPr().unsetTblBorders();

        //获取静态数据
        ArrayList<HashMap> list = (ArrayList) param.get("item");
        for (int i = 0; i < list.size(); i++) {
            HashMap map = list.get(i);
            String label = MapUtil.getStr(map, "label");
            String value = MapUtil.getStr(map, "value");
            XWPFTableRow row = table.getRow(i);
            row.setHeight(600);
            List<XWPFTableCell> tableCells = row.getTableCells();
            //设置第二列宽度
            CTTcPr ctTcPr = tableCells.get(1).getCTTc().addNewTcPr();
            CTTblWidth ctTblWidth = ctTcPr.addNewTcW();
            ctTblWidth.setW(BigInteger.valueOf(2800));
            //设置当前行第一个单元格内容
            paragraph = tableCells.get(0).getParagraphArray(0);
            run = paragraph.createRun();
            run.setFontFamily("Microsoft YaHei");
            run.setText(label + "    ");
            //设置当前行第二个单元格内容
            paragraph = tableCells.get(1).getParagraphArray(0);
            paragraph.setBorderBottom(Borders.BABY_RATTLE);
            run = paragraph.createRun();
            run.setFontFamily("Microsoft YaHei");
            run.setText("    " + value);
        }
    }

    public void createWelcome(XWPFDocument doc, HashMap param) {
        //创建标题文字
        XWPFParagraph paragraph = doc.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setFontSize(18);
        run.setFontFamily("Microsoft YaHei");
        run.setText("健康体检报告");
        CTP ctp = paragraph.getCTP();
        CTPPr ctpPr = ctp.addNewPPr();
        CTSpacing ctSpacing = ctpPr.addNewSpacing();
        //设置行间距
        ctSpacing.setLineRule(STLineSpacingRule.EXACT);
        ctSpacing.setLine(BigInteger.valueOf(420));
        //设置上外填充，实现另起1页
        ctSpacing.setBeforeLines(BigInteger.valueOf(2000));
        //设置下外填充
        ctSpacing.setAfterLines(BigInteger.valueOf(200));
        /**
         * 创建第一行文字
         */
        paragraph = doc.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(10);
        run.setFontFamily("Microsoft YaHei");
        String name = MapUtil.getStr(param, "name");
        String sex = MapUtil.getStr(param, "sex");
        String temp = name + (sex.equals("男") ? "先生" : "女士");
        run.setText("尊敬的" + temp + ", 您好！ ");
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setLineRule(STLineSpacingRule.EXACT);
        ctSpacing.setLine(BigInteger.valueOf(380));
        ctSpacing.setAfterLines(BigInteger.valueOf(50));
        /**
         * 创建第2行
         */
        paragraph = doc.createParagraph();
        //段首缩进两个汉字
        paragraph.setIndentationFirstLine(600);
        run = paragraph.createRun();
        run.setFontSize(10);
        run.setFontFamily("Microsoft YaHei");
        run.setText("感谢您到北京市耀东大健康中心体检。现将您的体检结果汇总如下，请您认真阅读体检结果和建议。如有疑问，请您来院或者致电本中心服务电话010-24813397，我们将安排专业人员为您答疑解惑。欢迎对我们的工作提出批评和建议。祝您健康！");
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setLineRule(STLineSpacingRule.EXACT);
        ctSpacing.setLine(BigInteger.valueOf(380));
        ctSpacing.setAfterLines(BigInteger.valueOf(100));
    }

    public void createCustomerInfo(XWPFDocument doc, HashMap param) {
        XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setFontSize(14);
        run.setFontFamily("Microsoft YaHei");
        run.setText("体检人信息");
        //设置上下外填充
        CTP ctp = paragraph.getCTP();
        CTPPr ctpPr = ctp.addNewPPr();
        ctpPr = ctp.addNewPPr();
        CTSpacing ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setLine(BigInteger.valueOf(200));
        ctSpacing.setAfterLines(BigInteger.valueOf(100));
        //插入表格
        XWPFTable table = doc.createTable(4, 6);
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        //设置表格总宽度
        tblPr.getTblW().setType(STTblWidth.DXA);
        tblPr.getTblW().setW(BigInteger.valueOf(9850));
        //第1行
        XWPFTableRow row = table.getRow(0);
        row.setHeight(550);
        List<XWPFTableCell> tableCells = row.getTableCells();
        //设置姓名Label
        XWPFTableCell cell = tableCells.get(0);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cell.setColor("f0f0f0");
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("姓名");
        //设置列宽
        CTTcPr ctTcPr = cell.getCTTc().addNewTcPr();
        CTTblWidth ctTblWidth = ctTcPr.addNewTcW();
        ctTblWidth.setW(BigInteger.valueOf(1500));

        //设置姓名 value
        cell = tableCells.get(1);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText(MapUtil.getStr(param, "name"));

        //设置 性别 label
        cell = tableCells.get(2);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cell.setColor("f0f0f0");
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("性别");
        //设置列宽
        ctTcPr = cell.getCTTc().addNewTcPr();
        ctTblWidth = ctTcPr.addNewTcW();
        ctTblWidth.setW(BigInteger.valueOf(1500));
        //设置性别 value
        cell = tableCells.get(3);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText(MapUtil.getStr(param, "sex"));
        //设置列宽
        ctTcPr = cell.getCTTc().addNewTcPr();
        ctTblWidth = ctTcPr.addNewTcW();
        ctTblWidth.setW(BigInteger.valueOf(1200));

        //设置 生日 label
        cell = tableCells.get(4);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cell.setColor("f0f0f0");
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("出生日期");
        //设置列宽
        ctTcPr = cell.getCTTc().addNewTcPr();
        ctTblWidth = ctTcPr.addNewTcW();
        ctTblWidth.setW(BigInteger.valueOf(1500));

        //设置生日 value
        cell = tableCells.get(5);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText(MapUtil.getStr(param, "birthday"));

        //第二行
        row = table.getRow(1);
        row.setHeight(550);
        tableCells = row.getTableCells();

        //设置电话 Label
        cell = tableCells.get(0);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cell.setColor("f0f0f0");
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("电话");
        //设置列宽
        ctTcPr = cell.getCTTc().addNewTcPr();
        ctTblWidth = ctTcPr.addNewTcW();
        ctTblWidth.setW(BigInteger.valueOf(1500));
        //设置电话 value
        cell = tableCells.get(1);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText(MapUtil.getStr(param, "tel"));

        //设置年龄 Label
        cell = tableCells.get(2);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cell.setColor("f0f0f0");
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("年龄");

        //设置年龄 value
        cell = tableCells.get(3);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText(MapUtil.getStr(param, "age"));

        //设置体验日Label
        cell = tableCells.get(4);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cell.setColor("f0f0f0");
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("体检日期");
        //设置体验日 value
        cell = tableCells.get(5);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText(MapUtil.getStr(param, "date"));

        //第3行
        row = table.getRow(2);
        row.setHeight(550);
        tableCells = row.getTableCells();

        //设置工作单位Label
        cell = tableCells.get(0);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cell.setColor("f0f0f0");
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("单位名称");
        //设置 value
        cell = tableCells.get(1);
        cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("    " + param.get("company"));

        //给后面4个单元设置STMerge.CONTINUE,仍它们不显示，也就是当前单元格用了 5个单元格宽度
        for (int i = 2; i <= 5; i++) {
            cell = tableCells.get(i);
            cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        }

        //第4行
        row = table.getRow(3);
        row.setHeight(600);
        tableCells = row.getTableCells();

        //设置体检套餐Label
        cell = tableCells.get(0);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cell.setColor("f0f0f0");
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("体检套餐");
        //设置体检套餐 value
        cell = tableCells.get(1);
        cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("    " + param.get("goods"));

        //给后面4个单元设置STMerge.CONTINUE 让它们不显示，也就是当前单元格占用了5个单元格宽度
        for (int i = 2; i <= 5; i++) {
            cell = tableCells.get(i);
            cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        }
    }

    public void createCheckup(XWPFDocument doc, List<Map> list) {
        XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setFontSize(14);
        run.setFontFamily("Microsoft YaHei");
        run.setText("体检項目列表");
        //设置上下外填充
        CTP ctp = paragraph.getCTP();
        CTPPr ctpPr = ctp.addNewPPr();
        CTSpacing ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setBeforeLines(BigInteger.valueOf(200));
        ctSpacing.setAfterLines(BigInteger.valueOf(100));

        /**
         * 插入表格
         */
        XWPFTable table = doc.createTable(list.size() + 1, 3);
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.DXA);
        tblPr.getTblW().setW(BigInteger.valueOf(9850));
        //表头文字
        String[] array = {"序号", "体检科室", "体检内容"};
        //第1行 （表头行）
        XWPFTableRow row = table.getRow(0);
        row.setHeight(550);
        List<XWPFTableCell> tableCells = row.getTableCells();
        for (int i = 0; i < array.length; i++) {
            String text = array[i];
            XWPFTableCell cell = tableCells.get(i);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            cell.setColor("f0f0f0");
            paragraph = cell.getParagraphArray(0);
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontSize(9);
            run.setFontFamily("Microsoft YaHei");
            run.setText(text);
        }
        //生成表格中其余行
        String temp = null;
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            Map one = list.get(i);
            String place = MapUtil.getStr(one, "place");
            String item = MapUtil.getStr(one, "item");

            row = table.getRow(i + 1);
            row.setHeight(550);
            tableCells = row.getTableCells();

            //当前行的第二列（科室名称）
            XWPFTableCell cell = tableCells.get(1);
            STMerge.Enum val = null;
            //判断当前体验项目的名称跟上条体检项目的科室名称是否不相同
            if (!place.equals(temp)) {
                index++;
                temp = place;
                //当前单元格正常显示
                val = STMerge.RESTART;
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(val);
            } else {
                //当前单元格不显示，让上一个单元格体现垂直合并的效果
                val = STMerge.CONTINUE;
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(val);
            }

            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            paragraph = cell.getParagraphArray(0);
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontSize(9);
            run.setFontFamily("Microsoft YaHei");
            run.setText(place);

            //第1列（序号）
            cell = tableCells.get(0);
            cell.getCTTc().addNewTcPr().addNewVMerge().setVal(val);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            paragraph = cell.getParagraphArray(0);
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontSize(9);
            run.setFontFamily("Microsoft YaHei");
            run.setText(index + "");

            //第3列 （体检项目名称）
            cell = tableCells.get(2);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            paragraph = cell.getParagraphArray(0);
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontSize(9);
            run.setFontFamily("Microsoft YaHei");
            run.setText(item);
        }
    }

    public void createCheckupResultByTemplate_1(XWPFDocument doc, Map param) {
        String place = MapUtil.getStr(param, "place");
        List<Map> item = MapUtil.get(param, "item", List.class);
        String doctor = MapUtil.getStr(param, "doctorName");
        String date = MapUtil.getStr(param, "date");

        XWPFParagraph paragraph = doc.createParagraph();
        //设置段落下边框线
        paragraph.setBorderBottom(Borders.BABY_RATTLE);

        XWPFRun run = paragraph.createRun();
        run.setFontSize(14);
        run.setFontFamily("Microsoft YaHei");
        run.setTextHighlightColor("lightGray");
        run.setText(" 【 " + place + " 】体检结果 ");
        //设置上下外填充
        CTP ctp = paragraph.getCTP();
        CTPPr ctpPr = ctp.addNewPPr();
        CTSpacing ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setBeforeLines(BigInteger.valueOf(250));
        ctSpacing.setAfterLines(BigInteger.valueOf(150));

        //插入表格
        XWPFTable table = doc.createTable(item.size() + 1, 5);
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.DXA);
        tblPr.getTblW().setW(BigInteger.valueOf(9850));

        //设置表头
        XWPFTableRow row = table.getRow(0);
        row.setHeight(500);
        List<XWPFTableCell> tableCells = row.getTableCells();
        String[] array = {
                "序号#800",
                "检查项目#2200",
                "检查结果#3000",
                "单位#1200",
                "参考值#1300",
        };
        for (int i = 0; i <= 4; i++) {
            XWPFTableCell cell = tableCells.get(i);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            cell.setColor("f0f0f0");
            paragraph = cell.getParagraphArray(0);
            if (i != 2) {
                //非第三列的文字居中对齐
                paragraph.setAlignment(ParagraphAlignment.CENTER);
            } else {
                //第3列的文字左对齐
                paragraph.setIndentationLeft(200);
            }
            run = paragraph.createRun();
            run.setFontSize(9);
            run.setFontFamily("Microsoft YaHei");
            //表头文字
            String label = array[i].split("#")[0];
            //表头列宽
            int width = Integer.parseInt(array[i].split("#")[1]);
            run.setText(label);
            //设置列宽
            CTTcPr ctTcPr = cell.getCTTc().addNewTcPr();
            CTTblWidth ctTblWidth = ctTcPr.addNewTcW();
            ctTblWidth.setW(BigInteger.valueOf(width));
        }
        //设置体检结果
        for (int i = 0; i < item.size(); i++) {
            Map map = item.get(i);
            String name = MapUtil.getStr(map, "name");
            String value = MapUtil.getStr(map, "value");
            String unit = MapUtil.getStr(map, "unit");
            unit = (unit == null ? "" : unit);
            String standard = MapUtil.getStr(map, "standard");
            standard = (standard == null ? "" : standard);
            //当前行里面每个单元格的数据
            String[] temp = {
                    i + 1 + "",
                    name,
                    value,
                    unit,
                    standard
            };
            row = table.getRow(i + 1);
            row.setHeight(500);
            tableCells = row.getTableCells();
            for (int j = 0; j < temp.length; j++) {
                XWPFTableCell cell = tableCells.get(j);
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                paragraph = cell.getParagraphArray(0);
                if (j != 2) {
                    //非第三列的文字居中对齐
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                } else {
                    //第3列的文字左对齐
                    paragraph.setIndentationLeft(200);
                }
                run = paragraph.createRun();
                run.setFontSize(9);
                run.setFontFamily("Microsoft YaHei");
                run.setText(temp[j]);
            }
        }
        //设置体检医生和日期
        paragraph = doc.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("体检医生：" + doctor + "\t\t\t\t\t\t\t\t\t日期：" + date);
        //设置上下外填充
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing.setBeforeLines(BigInteger.valueOf(100));
        ctSpacing.setAfterLines(BigInteger.valueOf(150));
    }

    @SneakyThrows
    public void createCheckupResultByTemplate_2(XWPFDocument doc, Map param) {
        String place = MapUtil.getStr(param, "place");
        List<Map> item = (List<Map>) param.get("item");
        String doctor = MapUtil.getStr(param, "doctorName");
        String date = MapUtil.getStr(param, "date");
        String image = MapUtil.getStr(param, "image");

        XWPFParagraph paragraph = doc.createParagraph();
        paragraph.setBorderBottom(Borders.BABY_RATTLE);

        XWPFRun run = paragraph.createRun();
        run.setFontSize(14);
        run.setFontFamily("Microsoft YaHei");
        run.setTextHighlightColor("lightGray");
        run.setText(" 【 " + place + "体检结果 】 ");
        //设置上下外填充
        CTP ctp = paragraph.getCTP();
        CTPPr ctpPr = ctp.addNewPPr();
        CTSpacing ctSpacing = ctpPr.addNewSpacing();
        ctSpacing.setBeforeLines(BigInteger.valueOf(250));
        ctSpacing.setAfterLines(BigInteger.valueOf(150));
        /**
         * 插入图片
         */
        if (image != null) {
            URL url = new URL(image);
            InputStream in = url.openStream();
            BufferedImage bufferedImage = ImageIO.read(url);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            //调整图片尺寸适配宽度
            double scaling = 1.0;
            if (width > 72 * 9.13) {
                scaling = (72.0 * 9.13) / width;
            }
            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            //设置图片下外填充
            ctp = paragraph.getCTP();
            ctpPr = ctp.addNewPPr();
            ctSpacing = ctpPr.addNewSpacing();
            ctSpacing.setAfterLines(BigInteger.valueOf(100));
            //往段落中插入图片
            run = paragraph.createRun();
            run.addPicture(in, Document.PICTURE_TYPE_JPEG, "",
                    Units.pixelToEMU((int) (width * scaling)),
                    Units.pixelToEMU((int) (height * scaling))
            );
        }
        //插入表格
        XWPFTable table = doc.createTable(item.size() + 1, 5);
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.DXA);
        tblPr.getTblW().setW(BigInteger.valueOf(9850));
        //设置表头
        XWPFTableRow row = table.getRow(0);
        row.setHeight(500);
        List<XWPFTableCell> tableCells = row.getTableCells();
        String[] array = {"序号#800", "检查项目#2200", "检查结果#3000", "单位#1200", "参考值#1300",};
        for (int i = 0; i <= 4; i++) {
            XWPFTableCell cell = tableCells.get(i);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            cell.setColor("f0f0f0");
            paragraph = cell.getParagraphArray(0);
            if (i != 2) {
                //非第三列的文字居中对齐
                paragraph.setAlignment(ParagraphAlignment.CENTER);
            } else {
                //第3列的文字左对齐
                paragraph.setIndentationLeft(200);
            }
            run = paragraph.createRun();
            run.setFontSize(9);
            run.setFontFamily("Microsoft YaHei");
            String label = array[i].split("#")[0];
            int width = Integer.parseInt(array[i].split("#")[1]);
            run.setText(label);
            //设置列宽
            CTTcPr ctTcPr = cell.getCTTc().addNewTcPr();
            CTTblWidth ctTblWidth = ctTcPr.addNewTcW();
            ctTblWidth.setW(BigInteger.valueOf(width));
        }
        //设置体检结果
        for (int i = 0; i < item.size(); i++) {
            Map map = item.get(i);
            String name = MapUtil.getStr(map, "name");
            String value = MapUtil.getStr(map, "value");
            String unit = MapUtil.getStr(map, "unit");
            unit = (unit == null ? "" : unit);
            String standard = MapUtil.getStr(map, "standard");
            standard = (standard == null ? "" : standard);
            //当前行里面每个单元格的数据
            String[] temp = {
                    i + 1 + "",
                    name,
                    value,
                    unit,
                    standard
            };
            row = table.getRow(i + 1);
            row.setHeight(500);
            tableCells = row.getTableCells();
            for (int j = 0; j < temp.length; j++) {
                XWPFTableCell cell = tableCells.get(j);
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                paragraph = cell.getParagraphArray(0);
                run = paragraph.createRun();
                run.setFontSize(9);
                run.setFontFamily("Microsoft YaHei");
                run.setText(temp[j]);
                if (j != 2) {
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    run.setText(temp[j]);
                } else {
                    paragraph.setAlignment(ParagraphAlignment.LEFT);
                    paragraph.setIndentationLeft(200);
                    array = temp[j].split("#");
                    for (int k = 0; k < array.length; k++) {
                        String one = array[k];
                        run.setText(one);
                        if (k < array.length - 1) {
                            //输出换行符
                            run.addBreak();
                        }
                    }
                }
            }
        }
        //设置体检医生和日期
        paragraph = doc.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(9);
        run.setFontFamily("Microsoft YaHei");
        run.setText("体检医生：" + doctor + "\t\t\t\t\t\t\t\t\t日期：" + date);
        //设置上下外填充
        ctp = paragraph.getCTP();
        ctpPr = ctp.addNewPPr();
        ctSpacing.setBeforeLines(BigInteger.valueOf(100));
        ctSpacing.setAfterLines(BigInteger.valueOf(150));
    }


    public XWPFDocument createReport(HashMap map) throws Exception {
        XWPFDocument doc = new XWPFDocument();
        //设置文档的页边距
        CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setLeft(BigInteger.valueOf(1200));
        pageMar.setRight(BigInteger.valueOf(1200));
        pageMar.setTop(BigInteger.valueOf(1000));
        pageMar.setBottom(BigInteger.valueOf(1000));

        //设置页脚显示页数
        XWPFFooter footer = doc.createFooter(HeaderFooterType.DEFAULT);
        XWPFParagraph paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");
        paragraph.createRun();

        String uuid = MapUtil.getStr(map, "uuid");
        String name = MapUtil.getStr(map, "name");
        String sex = MapUtil.getStr(map, "sex");
        String tel = MapUtil.getStr(map, "tel");
        String birthday = MapUtil.getStr(map, "birthday");
        String age = MapUtil.getStr(map, "age");
        String company = MapUtil.getStr(map, "company");
        String date = MapUtil.getStr(map, "date");
        String goods = MapUtil.getStr(map, "goods");
        List<Map> checkup = MapUtil.get(map, "checkup", List.class);
        List<Map> result = MapUtil.get(map, "result", List.class);

        HashMap param0 = new HashMap() {{
            put("uuid", "01GWY35ZBXW01682HXM7N44DSB");
            put("item", new ArrayList<>() {{
                add(new HashMap<>() {{
                    put("label", "姓    名：");
                    put("value", name);
                }});
                add(new HashMap<>() {{
                    put("label", "性    别：");
                    put("value", sex);
                }});
                add(new HashMap<>() {{
                    put("label", "单    位：");
                    put("value", company);
                }});
                add(new HashMap<>() {{
                    put("label", "日    期：");
                    put("value", date);
                }});
            }});
            put("name", name);
            put("sex", sex);
            put("birthday", date);
            put("age", age);
            put("tel", tel);
            put("date", date);
            put("company", company);
            put("goods", goods);
        }};
        createCover(doc, param0);
        createWelcome(doc, param0);
        createCustomerInfo(doc, param0);
        createCheckup(doc, checkup);
        result.forEach(one -> {
            String template = MapUtil.getStr(one, "template");
            if ("模板1".equals(template)) {
                createCheckupResultByTemplate_1(doc, one);
            } else if ("模板2".equals(template)) {
                createCheckupResultByTemplate_2(doc, one);
            }
        });
        return doc;
    }
}
