/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.columns.SeparationColumnIndices;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public class MassLibConverter {

	private static final Logger logger = Logger.getLogger(MassLibConverter.class);
	//
	private Pattern patternColumn = Pattern.compile("(INFO1:)(\\s+)(\\w+)(\\s+)(\\w+-?\\w+)");
	private Pattern patternIndices = Pattern.compile("(RETIND:)(\\s+)(\\d+:\\d+:?\\d+)(\\s+)(\\d*)");
	private Pattern patternTargets = Pattern.compile("(SCANDESC:)(\\s+)(\\d+)(\\s+)(')(.*)(')");

	/**
	 * 1:01
	 * 1:55
	 * 0:2:29
	 * 
	 * @param value
	 * @return int
	 */
	public int parseRetentionTime(String time) {

		String[] values = time.trim().split(":");
		int retentionTime = 0;
		//
		if(values.length == 2) {
			retentionTime += getInteger(values[0]) * IChromatogram.MINUTE_CORRELATION_FACTOR; // Minutes
			retentionTime += getInteger(values[1]) * IChromatogram.SECOND_CORRELATION_FACTOR; // Seconds
		} else if(values.length == 3) {
			retentionTime += getInteger(values[0]) * IChromatogram.HOUR_CORRELATION_FACTOR; // Hours
			retentionTime += getInteger(values[1]) * IChromatogram.MINUTE_CORRELATION_FACTOR; // Minutes
			retentionTime += getInteger(values[2]) * IChromatogram.SECOND_CORRELATION_FACTOR; // Seconds
		}
		//
		return retentionTime;
	}

	/**
	 * 600
	 * 700
	 * 
	 * @param index
	 * @return float
	 */
	public float parseRetentionIndex(String index) {

		float retentionIndex = getFloat(index.trim());
		return retentionIndex < 0 ? 0 : retentionIndex;
	}

	/**
	 * Return ISeparationColumnIndices via IProcessingInfo.
	 *
	 * @param file
	 * @return {@link IProcessingInfo}
	 */
	public IProcessingInfo<ISeparationColumnIndices> parseRetentionIndices(File file) {

		IProcessingInfo<ISeparationColumnIndices> processingInfo = new ProcessingInfo<>();
		ISeparationColumnIndices separationColumnIndices = new SeparationColumnIndices();
		ISeparationColumn separationColumn = separationColumnIndices.getSeparationColumn();
		/*
		 * *INFO1: 20m ZB-1, 60-9-300, Split 60 !GLOBAL1
		 * *RETIND: 1:01 600
		 * *RETIND: 1:19 700
		 * *RETIND: 1:55 800
		 * *RETIND: 2:56 900
		 * ...
		 * *INFO1: 30m WAX+ 60-9-230, split 40, !global1
		 * *RETIND: 0:2:29 800
		 * *RETIND: 0:2:50 900
		 * *RETIND: 0:3:24 1000
		 * *RETIND: 0:4:16 1100
		 * ...
		 */
		try {
			String content = FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
			/*
			 * Column
			 */
			String length = "";
			String name = "";
			Matcher matcherColumn = patternColumn.matcher(content);
			while(matcherColumn.find()) {
				length = matcherColumn.group(3).trim();
				name = matcherColumn.group(5).trim();
			}
			separationColumn.copyFrom(SeparationColumnFactory.getSeparationColumn(name, length, "", ""));
			/*
			 * Indices (RT/RI)
			 */
			Matcher matcherIndices = patternIndices.matcher(content);
			while(matcherIndices.find()) {
				int retentionTime = parseRetentionTime(matcherIndices.group(3));
				float retentionIndex = parseRetentionIndex(matcherIndices.group(5));
				separationColumnIndices.put(new RetentionIndexEntry(retentionTime, retentionIndex, ""));
			}
		} catch(IOException e) {
			logger.warn(e);
		}
		//
		processingInfo.setProcessingResult(separationColumnIndices);
		return processingInfo;
	}

	public IProcessingInfo<Map<Integer, String>> parseTargets(File file) {

		IProcessingInfo<Map<Integer, String>> processingInfo = new ProcessingInfo<>();
		Map<Integer, String> targets = new HashMap<>();
		//
		try {
			String content = FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
			Matcher matcherTargets = patternTargets.matcher(content);
			while(matcherTargets.find()) {
				int scan = getInteger(matcherTargets.group(3).trim());
				String name = matcherTargets.group(6).trim();
				targets.put(scan, name);
			}
		} catch(IOException e) {
			logger.warn(e);
		}
		//
		processingInfo.setProcessingResult(targets);
		return processingInfo;
	}

	private float getFloat(String value) {

		float result = 0.0f;
		try {
			result = Float.valueOf(value);
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return result;
	}

	private int getInteger(String value) {

		int result = 0;
		try {
			result = Integer.valueOf(value);
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return result;
	}
}
