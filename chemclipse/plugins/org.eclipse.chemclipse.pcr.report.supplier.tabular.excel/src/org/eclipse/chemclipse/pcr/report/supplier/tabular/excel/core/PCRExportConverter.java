/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.excel.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.pcr.converter.core.AbstractPlateExportConverter;
import org.eclipse.chemclipse.pcr.converter.core.IPlateExportConverter;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.pcr.model.core.Position;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.Utils;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.excel.l10n.HeaderMessages;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.excel.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMapping;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMappings;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.WellComparator;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PCRExportConverter extends AbstractPlateExportConverter implements IPlateExportConverter {

	private static final Logger logger = Logger.getLogger(PCRExportConverter.class);
	private static final String DESCRIPTION = "PCR Excel Export";

	@Override
	public IProcessingInfo<File> convert(File file, IPlate plate, IProgressMonitor monitor) {

		IProcessingInfo<File> processingInfo = new ProcessingInfo<>();
		//
		try (XSSFWorkbook workbook = new XSSFWorkbook();) {
			XSSFSheet sheet = workbook.createSheet(HeaderMessages.results);
			XSSFCellStyle style = workbook.createCellStyle();
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
			@SuppressWarnings("unused")
			XSSFFont defaultFont = workbook.createFont(); // otherwise the header font becomes default
			XSSFFont headerFont = workbook.createFont();
			headerFont.setBold(true);
			XSSFCellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFont(headerFont);
			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			//
			if(plate != null) {
				try {
					Map<String, String> headerDataMap = plate.getHeaderDataMap();
					List<String> sampleSubsets = new ArrayList<>(getSampleSubsets(plate));
					for(String sampleSubset : sampleSubsets) {
						printResultTable(plate, sampleSubset, sheet, style, headerStyle);
					}
					sheet.createRow(sheet.getLastRowNum() + 1);
					printValue(sheet, IPlate.DATE, headerDataMap);
					FileOutputStream fileout = new FileOutputStream(file);
					workbook.write(fileout);
					fileout.close();
					processingInfo.setProcessingResult(file);
				} catch(FileNotFoundException e) {
					logger.warn(e);
					processingInfo.addErrorMessage(DESCRIPTION, "File not found.");
				} catch(IOException e) {
					processingInfo.addErrorMessage(DESCRIPTION, "Input/Output problem.");
					logger.warn(e);
				}
			} else {
				processingInfo.addErrorMessage(DESCRIPTION, "The PCR plate is not available.");
			}
		} catch(IOException e1) {
			logger.warn(e1);
		}
		//
		return processingInfo;
	}

	private Set<String> getSampleSubsets(IPlate plate) {

		Set<String> sampleSubsets = new HashSet<>();
		for(IWell well : plate.getWells()) {
			Map<String, String> dataMap = well.getHeaderDataMap();
			String sampleSubset = dataMap.getOrDefault(IWell.SAMPLE_SUBSET, "");
			if(!sampleSubset.isEmpty()) {
				sampleSubsets.add(sampleSubset);
			}
		}
		return sampleSubsets;
	}

	private void printResultTable(IPlate plate, String targetSubset, XSSFSheet sheet, XSSFCellStyle style, XSSFCellStyle headerStyle) {

		Set<String> ignoredSubsets = PreferenceSupplier.getIgnoredSubsets();
		if(ignoredSubsets.stream().anyMatch(targetSubset::equalsIgnoreCase)) {
			return;
		}
		if(Utils.isEmpty(plate, targetSubset)) {
			return;
		}
		//
		XSSFRow header = sheet.createRow(sheet.getLastRowNum() + 1);
		XSSFCell positionHeaderCell = header.createCell(0);
		positionHeaderCell.setCellValue(HeaderMessages.pos);
		positionHeaderCell.setCellStyle(headerStyle);
		XSSFCell nameHeaderCell = header.createCell(1);
		nameHeaderCell.setCellValue(HeaderMessages.name);
		nameHeaderCell.setCellStyle(headerStyle);
		XSSFCell requestHeaderCell = header.createCell(2);
		requestHeaderCell.setCellValue(HeaderMessages.analysis);
		requestHeaderCell.setCellStyle(headerStyle);
		XSSFCell subsetHeaderCell = header.createCell(3);
		subsetHeaderCell.setCellValue(HeaderMessages.subset);
		subsetHeaderCell.setCellStyle(headerStyle);
		//
		ChannelMappings channelMappings = PreferenceSupplier.getChannelMappings();
		for(ChannelMapping channelMapping : channelMappings) {
			if(channelMapping.getSubset().equalsIgnoreCase(targetSubset)) {
				XSSFCell dataCell = header.createCell(3 + channelMapping.getChannel());
				dataCell.setCellValue(channelMapping.getLabel());
				dataCell.setCellStyle(headerStyle);
			}
		}
		String separator = PreferenceSupplier.getAnalysisSeparator();
		boolean empty = true;
		WellComparator wellComparator = new WellComparator();
		Set<IWell> sortedWells = new TreeSet<>(wellComparator);
		sortedWells.addAll(plate.getWells());
		for(IWell well : sortedWells) {
			if(!well.isEmptyMeasurement()) {
				/*
				 * Sample Subset
				 */
				Position position = well.getPosition();
				Map<String, String> dataMap = well.getHeaderDataMap();
				String sampleSubset = dataMap.getOrDefault(IWell.SAMPLE_SUBSET, "");
				if(isSubsetMatch(sampleSubset, targetSubset)) {
					XSSFRow data = sheet.createRow(sheet.getLastRowNum() + 1);
					String sampleNumber = dataMap.getOrDefault(IWell.SAMPLE_ID, "");
					String request = "";
					if(separator != null && !separator.isEmpty()) {
						String[] sampleSplit = sampleNumber.split(separator);
						sampleNumber = sampleSplit[0];
						if(sampleSplit.length > 1) {
							request = sampleSplit[1];
						}
					}
					XSSFCell positionCell = data.createCell(0);
					positionCell.setCellValue(position.getRow() + position.getColumn());
					positionCell.setCellStyle(style);
					XSSFCell sampleNumberCell = data.createCell(1);
					sampleNumberCell.setCellValue(sampleNumber);
					sampleNumberCell.setCellStyle(style);
					XSSFCell requestCell = data.createCell(2);
					requestCell.setCellValue(request);
					requestCell.setCellStyle(style);
					XSSFCell subsetCell = data.createCell(3);
					String subset = dataMap.getOrDefault(IWell.SAMPLE_SUBSET, "");
					subsetCell.setCellValue(subset);
					subsetCell.setCellStyle(style);
					//
					List<Integer> keys = new ArrayList<>(well.getChannels().keySet());
					Collections.sort(keys);
					int cellNumber = 4;
					for(int key : keys) {
						IChannel channel = well.getChannels().get(key);
						double crossingPoint = channel.getCrossingPoint();
						XSSFCell channelCell = data.createCell(cellNumber);
						//
						if(channelCell.getColumnIndex() <= header.getLastCellNum() - 1) {
							channelCell.setCellStyle(style);
						}
						//
						if(crossingPoint > 0) {
							empty = false;
							channelCell.setCellValue(crossingPoint);
						}
						cellNumber++;
					}
				}
			}
		}
		if(empty) {
			sheet.removeRow(header);
		}
		if(!empty) {
			sheet.createRow(sheet.getLastRowNum() + 1);
		}
		for(int i = 0; i < 7; i++) {
			sheet.autoSizeColumn(i);
		}
		sheet.setColumnWidth(1, 11 * 480);
	}

	private boolean isSubsetMatch(String sampleSubset, String targetSubset) {

		boolean isMatch = false;
		if(targetSubset == null) {
			isMatch = true;
		} else {
			isMatch = sampleSubset.equals(targetSubset);
		}
		return isMatch;
	}

	private void printValue(XSSFSheet sheet, String key, Map<String, String> data) {

		XSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
		XSSFCell keyCell = row.createCell(0);
		keyCell.setCellValue(key);
		XSSFCell dataCell = row.createCell(0);
		dataCell.setCellValue(data.getOrDefault(key, ""));
	}
}
