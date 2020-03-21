/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.FeatureDataMatrix;

public class ExportData {

	private static final String DEF_CSV_SEPARATOR = "\t";
	private static final String DEF_FILE_NAME = "data";
	private String SHEET_NAME = "data";
	private FeatureDataMatrix featureDataMatrix;

	public ExportData(FeatureDataMatrix featureDataMatrix) {
		this.featureDataMatrix = featureDataMatrix;
	}

	private void createWorkBook(Workbook workbook) {

		Sheet spreadsheet = workbook.createSheet(SHEET_NAME);
		// for(int i = 0; i < data.getRowCount(); i++) {
		// Row row = spreadsheet.createRow(i);
		// int tableColumn = 0;
		// for(int j = 0; j < data.getColumnCount(); j++) {
		// Object d = data.getDataValue(j, i);
		// if(d instanceof Double) {
		// Double value = (Double)d;
		// if(value.isNaN()) {
		// row.createCell(tableColumn);
		// } else {
		// row.createCell(tableColumn).setCellValue(value);
		// }
		// } else if(d instanceof String) {
		// String value = (String)d;
		// row.createCell(tableColumn).setCellValue(value);
		// } else if(d instanceof Integer) {
		// Integer value = (Integer)d;
		// row.createCell(tableColumn).setCellValue(value);
		// } else if(d instanceof Boolean) {
		// Boolean value = (Boolean)d;
		// row.createCell(tableColumn).setCellValue(value);
		// } else {
		// row.createCell(tableColumn).setCellValue("Format is not supported");
		// }
		// tableColumn++;
		// }
		// }
	}

	// public void exportTableDialog(Display display) {
	//
	// FileDialog dialog = new FileDialog(display.getActiveShell(), SWT.SAVE);
	// dialog.setFilterNames(new String[]{"Excel(*.xlsx)", "Excel(97-2003)(*.xls)", "CSV(*.csv)"});
	// dialog.setFilterExtensions(new String[]{"*.xlsx", "*.xls", "*.csv"});
	// dialog.setFileName(ExportData.DEF_FILE_NAME + ".xlsx");
	// dialog.setOverwrite(true);
	// String path = dialog.open();
	// if(path != null) {
	// try {
	// int filterIndex = dialog.getFilterIndex();
	// switch(filterIndex) {
	// case 1:
	// exportToXLS(path);
	// break;
	// case 2:
	// exportToCSV(path);
	// break;
	// default:
	// exportToXLSX(path);
	// break;
	// }
	// } catch(IOException e1) {
	// MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Warning", e1.getMessage());
	// }
	// }
	// }
	private void exportToCSV(String path) throws IOException {

		exportToCSV(path, DEF_CSV_SEPARATOR);
	}

	private void exportToCSV(String path, String separator) throws IOException {

		// Workbook workbook = new XSSFWorkbook();
		// createWorkBook(workbook);
		// Sheet sheet = workbook.getSheetAt(0);
		// StringBuffer buffer = new StringBuffer();
		// Iterator<Row> rowIterator = sheet.iterator();
		// while(rowIterator.hasNext()) {
		// Row row = rowIterator.next();
		// Iterator<Cell> cellIterator = row.cellIterator();
		// while(cellIterator.hasNext()) {
		// Cell cell = cellIterator.next();
		// switch(cell.getCellType()) {
		// case Cell.CELL_TYPE_BOOLEAN:
		// buffer.append(cell.getBooleanCellValue());
		// break;
		// case Cell.CELL_TYPE_NUMERIC:
		// cell.getNumericCellValue();
		// buffer.append(cell.getNumericCellValue());
		// break;
		// case Cell.CELL_TYPE_STRING:
		// buffer.append(cell.getStringCellValue());
		// break;
		// }
		// buffer.append(separator);
		// }
		// buffer.append('\n');
		// }
		// try (FileOutputStream out = new FileOutputStream(getFilePath(path, "csv"))) {
		// out.write(buffer.toString().getBytes());
		// }
	}

	private void exportToXLS(String path) throws IOException {

		Workbook workbook = new HSSFWorkbook();
		createWorkBook(workbook);
		try (FileOutputStream out = new FileOutputStream(getFilePath(path, "xls"))) {
			workbook.write(out);
		}
	}

	private void exportToXLSX(String path) throws IOException {

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
}
