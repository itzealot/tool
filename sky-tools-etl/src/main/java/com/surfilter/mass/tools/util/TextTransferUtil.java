package com.surfilter.mass.tools.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class TextTransferUtil {

	public static final int BATCH_SIZE = 2000;

	public static final Charset UTF_8 = StandardCharsets.UTF_8;
	public static final Charset GBK = Charset.forName("GBK");
	public static final String NEW_LINE = "\r\n";

	public static void xlsx2Csv(File src, File dir) {
		FileOutputStream fos = null;
		XSSFWorkbook wBook = null;
		BufferedWriter writer = null;

		try {
			fos = new FileOutputStream(dir);
			writer = new BufferedWriter(new OutputStreamWriter(fos, UTF_8));
			// Get the workbook object for XLSX file
			wBook = new XSSFWorkbook(new FileInputStream(src));
			// Get first sheet from the workbook
			XSSFSheet sheet = wBook.getSheetAt(0);
			Row row = null;
			Cell cell = null;
			Iterator<Row> rowIterator = sheet.iterator();
			List<String> lines = new ArrayList<String>(BATCH_SIZE);
			int index = 0;

			while (rowIterator.hasNext()) {
				row = rowIterator.next();

				Iterator<Cell> cellIterator = row.cellIterator();
				StringBuffer buffer = new StringBuffer();
				while (cellIterator.hasNext()) {
					cell = cellIterator.next();

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						buffer.append(cell.getBooleanCellValue() + ",");
						break;

					case Cell.CELL_TYPE_NUMERIC: // Excel 科学计数法以数字展示
						buffer.append(new BigDecimal(cell.getNumericCellValue()).toPlainString() + ",");
						break;

					case Cell.CELL_TYPE_STRING:
						buffer.append(cell.getStringCellValue().replaceAll(",", " ").trim() + ",");
						break;

					case Cell.CELL_TYPE_BLANK:
						buffer.append(",");
						break;

					default:
						buffer.append(cell.toString().replaceAll(",", " ").trim() + ",");
					}
				}

				String line = buffer.toString();
				lines.add(line.substring(0, line.length() - 1));
				lines.add(NEW_LINE);
				index++;

				if (index >= BATCH_SIZE) {
					for (String l : lines) {
						writer.write(l);
					}
					index = 0;
					lines.clear();
				}
			}

			if (index > 0) {
				for (String line : lines) {
					writer.write(line);
				}
				lines.clear();
			}

			System.out.println(src.getName() + "---->" + dir.getName() + ", xlsx transfer to csv success.");
		} catch (IOException e) {
			System.out.println(src.getName() + "---->" + dir.getName() + ", xlsx transfer to csv failed.");
			e.printStackTrace();
		} finally {
			Closeables.close(wBook, writer);
		}
	}

	public static void xlsx2Csv(String src, String dir) {
		xlsx2Csv(new File(src), new File(dir));
	}

	public static void xls2Csv(String src, String dir) {
		xls2Csv(new File(src), new File(dir));
	}

	public static void xls2Csv(File src, File dir) {
		HSSFWorkbook readWorkbook = null;
		BufferedWriter writer = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(dir);
			readWorkbook = new HSSFWorkbook(new FileInputStream(src));
			writer = new BufferedWriter(new OutputStreamWriter(fos, UTF_8));
			HSSFSheet sourceSheet = readWorkbook.getSheetAt(0);
			HSSFRow sourceRow = null;
			HSSFCell cell = null;

			List<String> lines = new ArrayList<>(BATCH_SIZE);
			int index = 0;

			for (int i = 0; i <= sourceSheet.getLastRowNum(); i++) {
				StringBuffer buffer = new StringBuffer();

				sourceRow = sourceSheet.getRow(i);
				for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
					cell = sourceRow.getCell(j);
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						buffer.append(cell.getBooleanCellValue() + ",");
						break;
					case Cell.CELL_TYPE_NUMERIC:
						// Excel 科学计数法以数字展示
						buffer.append(new BigDecimal(cell.getNumericCellValue()).toPlainString() + ",");
						break;
					case Cell.CELL_TYPE_STRING:
						buffer.append(cell.getStringCellValue().replaceAll(",", " ").trim() + ",");
						break;

					case Cell.CELL_TYPE_BLANK:
						buffer.append(",");
						break;
					default:
						buffer.append(cell.toString().replaceAll(",", " ").trim() + ",");
					}
				}

				String line = buffer.toString();
				lines.add(line.substring(0, line.length() - 1));
				lines.add(NEW_LINE);
				index++;

				// batch write
				if (index >= BATCH_SIZE) {
					for (String l : lines) {
						writer.write(l);
					}
					index = 0;
					lines.clear();
				}
			}

			if (index > 0) {
				for (String line : lines) {
					writer.write(line);
				}
				index = 0;
				lines.clear();
			}
			System.out.println(src.getName() + "---->" + dir.getName() + ", xls transfer to csv success.");
		} catch (Exception e) {
			System.out.println(src.getName() + "---->" + dir.getName() + ", xls transfer to csv failed.");
			e.printStackTrace();
		} finally {
			Closeables.close(readWorkbook, writer);
		}
	}

	private TextTransferUtil() {
	}
}
