/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.TableProvider;

public class ExportData {

	public static final String DEF_CSV_SEPARATOR = "\t";
	public static final String DEF_FILE_NAME = "data";
	private ExportDataSupplier data;
	private boolean exportGroupMeans;
	private boolean exportSamples;
	private String SHEET_NAME = "data";

	public ExportData(ExportDataSupplier exportDataSupplier) {
		this.data = exportDataSupplier;
		exportGroupMeans = true;
		exportSamples = true;
	}

	private void createWorkBook(Workbook workbook) {

		Sheet spreadsheet = workbook.createSheet(SHEET_NAME);
		for(int i = 0; i < data.getRowCount(); i++) {
			Row row = spreadsheet.createRow(i);
			int tableColumn = 0;
			for(int j = 0; j < data.getColumnCount(); j++) {
				if(!exportGroupMeans && data.getDataType(j).equals(TableProvider.COLUMN_LABEL_GROUP_DATA)) {
					continue;
				}
				if(!exportSamples && data.getDataType(j).equals(TableProvider.COLUMN_LABEL_SAMPLE_DATA)) {
					continue;
				}
				Object d = data.getDataValue(j, i);
				if(d instanceof Double) {
					Double value = (Double)d;
					row.createCell(tableColumn).setCellValue(value);
				} else if(d instanceof String) {
					String value = (String)d;
					row.createCell(tableColumn).setCellValue(value);
				} else if(d instanceof Integer) {
					Integer value = (Integer)d;
					row.createCell(tableColumn).setCellValue(value);
				} else if(d instanceof Boolean) {
					Boolean value = (Boolean)d;
					row.createCell(tableColumn).setCellValue(value);
				} else {
					row.createCell(tableColumn).setCellValue("Format is not supported");
				}
				tableColumn++;
			}
		}
	}

	public void export(String path) throws IOException {

		String extension = FilenameUtils.getExtension(path);
		switch(extension) {
			case "xls":
				exportToXLS(path);
				break;
			case "csv":
				exportToCSV(path);
				break;
			default:
				exportToXLSX(path);
				break;
		}
	}

	public void exportToCSV(String path) throws IOException {

		exportToCSV(path, DEF_CSV_SEPARATOR);
	}

	public void exportToCSV(String path, String separator) throws IOException {

		Workbook workbook = new XSSFWorkbook();
		createWorkBook(workbook);
		Sheet sheet = workbook.getSheetAt(0);
		StringBuffer buffer = new StringBuffer();
		Iterator<Row> rowIterator = sheet.iterator();
		while(rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			while(cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				switch(cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						buffer.append(cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						buffer.append(cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						buffer.append(cell.getStringCellValue());
						break;
				}
				buffer.append(separator);
			}
			buffer.append('\n');
		}
		try (FileOutputStream out = new FileOutputStream(getFilePath(path, "csv"))) {
			out.write(buffer.toString().getBytes());
		}
	}

	public void exportToXLS(String path) throws IOException {

		Workbook workbook = new HSSFWorkbook();
		createWorkBook(workbook);
		try (FileOutputStream out = new FileOutputStream(getFilePath(path, "xls"))) {
			workbook.write(out);
		}
	}

	public void exportToXLSX(String path) throws IOException {

		Workbook workbook = new XSSFWorkbook();
		createWorkBook(workbook);
		try (FileOutputStream out = new FileOutputStream(getFilePath(path, "xlsx"))) {
			workbook.write(out);
		}
	}

	private File getFilePath(String path, String extension) {

		File file = new File(path);
		String fileName = file.getName();
		if(!fileName.isEmpty()) {
			if(!fileName.endsWith("." + extension)) {
				path = path + "." + extension;
			}
		} else {
			path = path + File.pathSeparator + DEF_FILE_NAME + "." + extension;
		}
		return new File(path);
	}

	public boolean isExportGroupMeans() {

		return exportGroupMeans;
	}

	public boolean isExportSamples() {

		return exportSamples;
	}

	public void setExportGroupMeans(boolean exportGroupMeans) {

		this.exportGroupMeans = exportGroupMeans;
	}

	public void setExportSamples(boolean exportSamples) {

		this.exportSamples = exportSamples;
	}
}
