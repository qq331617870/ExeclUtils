package com.wusha.execlutils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExeclUtils {

    /**
     * 创建一个csv文件的工具类
     *
     * @param inputStream 数据流
     * @throws Exception
     */
    public static List<String> ReadExcelUtilsCSV(InputStream inputStream) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        List<String> list = new ArrayList<String>();
        String stemp;
        while ((stemp = br.readLine()) != null) {
            list.add(stemp);
        }
        inputStream.close();
        br.close();
        return list;
    }

    /**
     * 创建一个csv文件的工具类
     *
     * @param filePath 文件路径
     * @throws Exception
     */
    public static List<String> ReadExcelUtilsCSV(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        List<String> list = new ArrayList<String>();
        String stemp;
        while ((stemp = br.readLine()) != null) {
            list.add(stemp);
        }
        br.close();
        return list;
    }

    /**
     * XLS数据流格式
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static String ReadExcelUtilsXLS(InputStream inputStream) throws Exception {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        int rowsCount = hssfSheet.getPhysicalNumberOfRows();
        FormulaEvaluator formulaEvaluator = hssfWorkbook.getCreationHelper().createFormulaEvaluator();
        String cellInfo = "";
        for (int r = 0; r < rowsCount; r++) {
            Row row = hssfSheet.getRow(r);
            int cellsCount = row.getPhysicalNumberOfCells();
            for (int c = 0; c < cellsCount; c++) {
                String value = getCellAsString(row, c, formulaEvaluator);
                if (cellInfo.equals("")) {
                    cellInfo = value;
                } else {
                    cellInfo = cellInfo + "," + value;
                }

            }
        }
        return cellInfo;
    }

    /**
     * XLS文件名格式
     *
     * @param fileName 文件名
     * @return
     * @throws Exception
     */
    public static String ReadExcelUtils(String fileName) throws Exception {
        InputStream inputStream = new FileInputStream(fileName);
        Workbook workbook = null;
        if (fileName.endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileName.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        }
        inputStream.close();
        Sheet sheet = workbook.getSheetAt(0);
        int rowsCount = sheet.getPhysicalNumberOfRows();
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        String cellInfo = "";
        for (int r = 0; r < rowsCount; r++) {
            Row row = sheet.getRow(r);
            int cellsCount = row.getPhysicalNumberOfCells();
            for (int c = 0; c < cellsCount; c++) {
                String value = getCellAsString(row, c, formulaEvaluator);
                if (cellInfo.equals("")) {
                    cellInfo = value;
                } else {
                    cellInfo = cellInfo + "," + value;
                }

            }
        }
        return cellInfo;
    }


    /**
     * XLS数据流格式
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static String ReadExcelUtilsXLSX(InputStream inputStream) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int rowsCount = sheet.getPhysicalNumberOfRows();
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        String cellInfo = "";
        for (int r = 0; r < rowsCount; r++) {
            Row row = sheet.getRow(r);
            int cellsCount = row.getPhysicalNumberOfCells();
            for (int c = 0; c < cellsCount; c++) {
                String value = getCellAsString(row, c, formulaEvaluator);
                if (cellInfo.equals("")) {
                    cellInfo = value;
                } else {
                    cellInfo = cellInfo + "," + value;
                }


            }
        }
        return cellInfo;
    }

    protected static String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = "" + cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("dd/MM/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = "" + numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = "" + cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {
            /* proper error handling should be here */

        }
        return value;
    }

    /**
     * 此方法为删除execl表格中的一行
     * 开始行数从1开始.
     * 结束行数建议写最大行数,不然中间会出现空缺.(有时间在更改)
     *
     * @param fileName 文件地址
     * @param startRow 开始的行数
     * @param endRow   结束的行数
     * @throws Exception
     */
    public static void removeColumn(String fileName, int startRow, int endRow) throws Exception {

        InputStream inputStream = null;
        inputStream = new FileInputStream(fileName);
        Workbook workbook = null;
        if (fileName.endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileName.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        }
        inputStream.close();
        Sheet sheet = workbook.getSheetAt(0);
         if (sheet == null) {
            return;
        }
         //开始从哪一行删除.然后哪一行结束.如果endrow不为list最大值则Excel中会出现空白整行
        sheet.shiftRows(startRow, endRow, -1);

        FileOutputStream os = new FileOutputStream(fileName);

        workbook.write(os);

        inputStream.close();

        os.close();

    }

    /**
     * 获取最大行
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static int getMaxRownum(String fileName) throws Exception {
        InputStream inputStream = new FileInputStream(fileName);
        Workbook workbook = null;
        if (fileName.endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileName.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        }
        inputStream.close();
        Sheet sheet = workbook.getSheetAt(0);
        //获取最大行
        int rowsCount = sheet.getPhysicalNumberOfRows();

        return rowsCount;
    }

    /**
     * 获取最大列
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static int getMaxColnum(String fileName) throws Exception {
        InputStream inputStream = new FileInputStream(fileName);
        Workbook workbook = null;
        if (fileName.endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileName.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        }
        //关闭流
        inputStream.close();
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(0);
        //获取最大的列
        int colnum = row.getPhysicalNumberOfCells();
        return colnum;
    }

    /**
     * 修改当前excle整行的整列数据
     *
     * @param fileName  文件名字
     * @param rownum    多少行
     * @param valueDate 这个行中列的值.
     * @throws IOException
     */
    public static void upWorkbook(String fileName, int rownum, List<String> valueDate) throws IOException {

        InputStream inputStream = null;
        inputStream = new FileInputStream(fileName);
        Workbook workbook = null;
        if (fileName.endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileName.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        }
        inputStream.close();
        //第一个sheet表
        Sheet sheet = workbook.getSheetAt(0);
        //如果已有行、列直接获取
        Row row = sheet.getRow(rownum);//得到行
        //循环出多少有多少列.把值赋进去
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            Cell cell = row.getCell(i);//得到列
            cell.setCellValue(valueDate.get(i));//写数据
        }
        FileOutputStream excelFileOutPutStream = new FileOutputStream(fileName);//写数据到这个路径上
        workbook.write(excelFileOutPutStream);
        //关闭流.应该放在finl中
        excelFileOutPutStream.flush();
        excelFileOutPutStream.close();
    }

}
