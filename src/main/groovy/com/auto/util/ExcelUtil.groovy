package com.auto.util

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ExcelUtil {


    public static enum ExcelType{XLS, XLSX};

    /**
     * 生成Excel
     *
     * @param data 数据行
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Workbook generExcel(ExcelType type, Map headerMap, final List<Map> data) {
        Workbook workbook; // 创建新的Excel工作薄
        if(type.compareTo(ExcelType.XLS)==0){//2003版
            workbook=new HSSFWorkbook();
        }else{
            workbook=new XSSFWorkbook();//默认2007版
        }

        Sheet sheet = workbook.createSheet();

        sheet.setDefaultColumnWidth(15); // 设置默认宽度
        Row row = null;


        row = sheet.createRow(0); //创建首行
        row.setHeightInPoints(18); //设置行高
        //设置头：
        int cnt = 0
        for(String key in headerMap.keySet()){
            Cell cell_top = row.createCell(cnt);
            cnt++
            cell_top.setCellValue(key); //设置标题
            CellStyle topStyle = workbook.createCellStyle(); //样式
            cell_top.setCellStyle(topStyle);
        }

        // 在索引1的位置建行
        row = sheet.createRow(1);
        //表头样式
        CellStyle hstyle = workbook.createCellStyle();
        hstyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());//设置背景色
        hstyle.setFillPattern(CellStyle.SOLID_FOREGROUND); //颜色填充模式
        hstyle.setBorderBottom(CellStyle.BORDER_THIN);//设置底线

        row.setRowStyle(hstyle);
        cnt = 0
        for(String value in headerMap.values()){
            if(value == ""){
                break
            }
            Cell c=row.createCell(cnt);
            c.setCellStyle(hstyle); //应用样式
            c.setCellValue(value);
            cnt++
        }


        if (data != null && data.size() != 0 ) {
            //map中的key
            List<String> dataKeys=new ArrayList<String>(data.get(0).keySet());

            for (int i = 0; i < data.size(); i++) {
                // 创建行,如果头只有一行，就只能+1
                int rowNum = headerMap.values()[0] == ""?1:2
                row = sheet.createRow(i + rowNum);
                // 得到行数据
                Map row_data = data.get(i);
                for (int n = 0; n < row_data.size(); n++) {
                    Cell cell = row.createCell(n);
                    Object cValue = row_data.get(dataKeys.get(n));
                    if (cValue != null && cValue instanceof java.lang.Number) {
                        cell.setCellValue(Double.valueOf(cValue.toString()));
                    } else if (cValue == null) {
                        // cell.setCellValue(0);
                    } else {
                        cell.setCellValue(String.valueOf(cValue));
                    }
                }
            }
        }

        return workbook;
    }


    public static void main(String[] args) {
//        try {
//            Map m=new HashMap<String, Object>();
//            m.put("c_createtime", "2014-5-9");
//            List<Map> data=new ArrayList<Map>();
//            data.add(m);
//            Workbook book=generExcel(ExcelType.XLSX, data);
//            book.write(new FileOutputStream(new File("./test-output/test.xlsx")));
//            System.out.println("生成结束");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Map headMap = [
                customerSid: "客户编码*",    customerName:"客户名称", outOrderNo:"外部订单号",logisticsSid:"物流公司编码", logisticsCode:"物流公司名称",
                logisticsName:"物流单号", providerInformation:"供应商", barcode:"SKU商品条形码",skuName: "商品名称","unit":"单位", spec:"规格",qty:"数量*",
                comment:"备注"

        ]
        Map data = null
        Workbook book=generExcel(ExcelType.XLSX,headMap, data);
        book.write(new FileOutputStream(new File("./test-output/test.xlsx")));
    }


}
