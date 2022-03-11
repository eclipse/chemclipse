/*******************************************************************************
 * Copyright (c) 2011, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.excel.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.converter.supplier.excel.internal.io.SpreadsheetWriter;
import org.eclipse.chemclipse.msd.converter.supplier.excel.internal.support.PathHelper;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriter extends AbstractChromatogramWriter implements IChromatogramMSDWriter {

	private static final Logger logger = Logger.getLogger(ChromatogramWriter.class);
	public static final String RT_MILLISECONDS_COLUMN = "RT(milliseconds)";
	public static final String RT_MINUTES_COLUMN = "RT(minutes)";
	public static final String RI_COLUMN = "RI";
	private static final String XML_ENCODING = "UTF-8";
	private static final String ABUNDANCE_STYLE = "abundance";

	public ChromatogramWriter() {

	}

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		XSSFWorkbook excelWorkbook = new XSSFWorkbook();
		XSSFSheet excelSheet = excelWorkbook.createSheet(chromatogram.getName());
		Map<String, XSSFCellStyle> styles = createStyles(excelWorkbook);
		String excelSheetReferenceName = excelSheet.getPackagePart().getPartName().getName();
		/*
		 * Excel Template
		 */
		File excelTemplate = new File(PathHelper.getStoragePath() + File.separator + "exceltemplace.xlsx");
		FileOutputStream outputStream = new FileOutputStream(excelTemplate);
		excelWorkbook.write(outputStream);
		outputStream.close();
		/*
		 * Temporary XML file
		 */
		File xmlDataFile = new File(PathHelper.getStoragePath() + File.separator + "datafile.xml");
		Writer writer = new OutputStreamWriter(new FileOutputStream(xmlDataFile), XML_ENCODING);
		try {
			writeChromatogram(writer, styles, chromatogram, monitor);
		} catch(Exception e) {
			throw new IOException("There has something gone wrong writing the temporary data file.");
		}
		writer.close();
		/*
		 * Merge the template and the data file.
		 */
		FileOutputStream resultOutputStream = new FileOutputStream(file);
		mergeTemplateAndData(excelTemplate, xmlDataFile, excelSheetReferenceName.substring(1), resultOutputStream);
		resultOutputStream.close();
		/*
		 * Delete the temporary files.
		 */
		excelTemplate.delete();
		xmlDataFile.delete();
	}

	private Map<String, XSSFCellStyle> createStyles(XSSFWorkbook excelWorkbook) {

		Map<String, XSSFCellStyle> styles = new HashMap<String, XSSFCellStyle>();
		XSSFDataFormat dataFormat = excelWorkbook.createDataFormat();
		/*
		 * Abundance values
		 */
		XSSFCellStyle style1 = excelWorkbook.createCellStyle();
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setDataFormat(dataFormat.getFormat("0.000"));
		styles.put(ABUNDANCE_STYLE, style1);
		/*
		 * Styles to use.
		 */
		return styles;
	}

	private void writeChromatogram(Writer writer, Map<String, XSSFCellStyle> styles, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		SpreadsheetWriter spreadsheetWriter = new SpreadsheetWriter(writer, XML_ENCODING);
		spreadsheetWriter.beginSheet();
		/*
		 * Chromatographic data.
		 */
		try {
			IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
			IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals();
			int startIon = extractedIonSignals.getStartIon();
			int stopIon = extractedIonSignals.getStopIon();
			/*
			 * Header and Scans
			 */
			int rownumber = writeHeader(spreadsheetWriter, 0, startIon, stopIon, monitor);
			writeScans(extractedIonSignals, spreadsheetWriter, styles, rownumber, startIon, stopIon, monitor);
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
		/*
		 * End of sheet.
		 */
		spreadsheetWriter.endSheet();
	}

	private int writeHeader(SpreadsheetWriter spreadsheetWriter, int rowNumber, int startIon, int stopIon, IProgressMonitor monitor) throws IOException {

		monitor.subTask("Write Header");
		int columnNumber = 0;
		spreadsheetWriter.insertRow(rowNumber++);
		spreadsheetWriter.createCell(columnNumber++, RT_MILLISECONDS_COLUMN);
		spreadsheetWriter.createCell(columnNumber++, RT_MINUTES_COLUMN);
		spreadsheetWriter.createCell(columnNumber++, RI_COLUMN);
		for(int ion = startIon; ion <= stopIon; ion++) {
			spreadsheetWriter.createCell(columnNumber++, ion);
		}
		spreadsheetWriter.endRow();
		return rowNumber;
	}

	private void writeScans(IExtractedIonSignals extractedIonSignals, SpreadsheetWriter spreadsheetWriter, Map<String, XSSFCellStyle> styles, int rowNumber, int startIon, int stopIon, IProgressMonitor monitor) throws IOException {

		int styleAbundance = styles.get(ABUNDANCE_STYLE).getIndex();
		/*
		 * Scans
		 */
		for(IExtractedIonSignal extractedIonSignal : extractedIonSignals.getExtractedIonSignals()) {
			/*
			 * RT (milliseconds)
			 * RT (minutes)
			 * RI
			 */
			monitor.subTask("Write Scan " + rowNumber);
			int columnNumber = 0;
			spreadsheetWriter.insertRow(rowNumber++);
			int milliseconds = extractedIonSignal.getRetentionTime();
			spreadsheetWriter.createCell(columnNumber++, milliseconds);
			spreadsheetWriter.createCell(columnNumber++, milliseconds / IChromatogramMSD.MINUTE_CORRELATION_FACTOR);
			spreadsheetWriter.createCell(columnNumber++, extractedIonSignal.getRetentionIndex());
			/*
			 * ion data
			 */
			for(int ion = startIon; ion <= stopIon; ion++) {
				spreadsheetWriter.createCell(columnNumber++, extractedIonSignal.getAbundance(ion), styleAbundance);
			}
			spreadsheetWriter.endRow();
		}
	}

	private void mergeTemplateAndData(File excelTemplate, File xmlDataFile, String excelSheetReferenceName, OutputStream outputStream) throws IOException {

		InputStream inputStream;
		ZipFile zipFile = new ZipFile(excelTemplate);
		ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
		@SuppressWarnings("unchecked")
		Enumeration<ZipEntry> zipEntries = (Enumeration<ZipEntry>)zipFile.entries();
		/*
		 * Copy all non data file elements to the output stream.
		 */
		while(zipEntries.hasMoreElements()) {
			ZipEntry zipEntry = zipEntries.nextElement();
			if(!zipEntry.getName().equals(excelSheetReferenceName)) {
				zipOutputStream.putNextEntry(new ZipEntry(zipEntry.getName()));
				inputStream = zipFile.getInputStream(zipEntry);
				copyDataStream(inputStream, zipOutputStream);
				inputStream.close();
			}
		}
		/*
		 * Copy the data file to the output stream.
		 */
		zipOutputStream.putNextEntry(new ZipEntry(excelSheetReferenceName));
		inputStream = new FileInputStream(xmlDataFile);
		copyDataStream(inputStream, zipOutputStream);
		inputStream.close();
		zipOutputStream.close();
		zipFile.close();
	}

	private void copyDataStream(InputStream inputStream, OutputStream outputStream) throws IOException {

		byte[] chunk = new byte[1024];
		int count;
		while((count = inputStream.read(chunk)) >= 0) {
			outputStream.write(chunk, 0, count);
		}
	}
}
