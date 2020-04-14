/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - extract constants into interface, don't overwrite data from constructor
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;
import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.text.ValueFormat;

public abstract class AbstractMeasurementInfo implements IMeasurementInfo {

	private static final long serialVersionUID = 4247159773898302230L;
	private static final Logger logger = Logger.getLogger(AbstractMeasurementInfo.class);
	//
	private Set<String> protectKeys;
	private Map<String, String> headerDataMap;
	private DateFormat dateFormat = ValueFormat.getDateFormatEnglish(ValueFormat.FULL_DATE_PATTERN);

	public AbstractMeasurementInfo(Map<String, String> headerData) {

		if(headerData != null)
			headerDataMap = new LinkedHashMap<>(headerData);
		else {
			headerDataMap = new LinkedHashMap<>();
		}
		//
		headerDataMap.putIfAbsent(OPERATOR, "");
		headerDataMap.putIfAbsent(DATE, dateFormat.format(new Date()));
		headerDataMap.putIfAbsent(MISC_INFO, "");
		headerDataMap.putIfAbsent(MISC_INFO_SEPARATED, "");
		headerDataMap.putIfAbsent(SHORT_INFO, "");
		headerDataMap.putIfAbsent(DETAILED_INFO, "");
		headerDataMap.putIfAbsent(SAMPLE_GROUP, "");
		headerDataMap.putIfAbsent(BARCODE, "");
		headerDataMap.putIfAbsent(BARCODE_TYPE, "");
		headerDataMap.putIfAbsent(SAMPLE_WEIGHT, Double.valueOf(0.0d).toString());
		headerDataMap.putIfAbsent(SAMPLE_WEIGHT_UNIT, "");
		headerDataMap.putIfAbsent(DATA_NAME, "");
		//
		protectKeys = new HashSet<String>(Arrays.asList(OPERATOR, DATE, MISC_INFO, MISC_INFO_SEPARATED, SHORT_INFO, DETAILED_INFO, SAMPLE_GROUP, BARCODE, BARCODE_TYPE, SAMPLE_WEIGHT, SAMPLE_WEIGHT_UNIT, DATA_NAME));
	}

	public AbstractMeasurementInfo() {

		this(null);
	}

	@Override
	public String getHeaderData(String key) {

		return headerDataMap.get(key);
	}

	@Override
	public String getHeaderDataOrDefault(String key, String defaultValue) {

		return headerDataMap.getOrDefault(key, defaultValue);
	}

	@Override
	public boolean headerDataContainsKey(String key) {

		return headerDataMap.containsKey(key);
	}

	@Override
	public void putHeaderData(String key, String value) {

		headerDataMap.put(key, value);
	}

	@Override
	public void removeHeaderData(String key) throws InvalidHeaderModificationException {

		if(protectKeys.contains(key)) {
			throw new InvalidHeaderModificationException("It's not possible to remove the following key: " + key);
		} else {
			headerDataMap.remove(key);
		}
	}

	@Override
	public Map<String, String> getHeaderDataMap() {

		return Collections.unmodifiableMap(headerDataMap);
	}

	protected void setHeaderDataMap(Map<String, String> headerDataMap) {

		this.headerDataMap = headerDataMap;
	}

	@Override
	public String getOperator() {

		return getHeaderData(OPERATOR);
	}

	@Override
	public void setOperator(String operator) {

		putHeaderData(OPERATOR, operator);
	}

	@Override
	public Date getDate() {

		try {
			return dateFormat.parse(getHeaderData(DATE));
		} catch(ParseException e) {
			logger.warn(e);
			return new Date();
		}
	}

	@Override
	public void setDate(Date date) {

		if(date != null) {
			putHeaderData(DATE, dateFormat.format(date));
		} else {
			putHeaderData(DATE, "");
		}
	}

	@Override
	public String getMiscInfo() {

		return getHeaderData(MISC_INFO);
	}

	@Override
	public void setMiscInfo(String miscInfo) {

		if(miscInfo != null) {
			String[] values = miscInfo.split(PreferenceSupplier.getMiscSeparator());
			if(values.length >= 2) {
				putHeaderData(MISC_INFO, values[0]);
				StringBuilder builder = new StringBuilder();
				for(int i = 1; i < values.length; i++) {
					builder.append(values[i].trim());
					builder.append(PreferenceSupplier.getMiscSeparatedDelimiter());
				}
				putHeaderData(MISC_INFO_SEPARATED, builder.toString().trim());
			} else {
				putHeaderData(MISC_INFO, miscInfo);
			}
		} else {
			putHeaderData(MISC_INFO, "");
			putHeaderData(MISC_INFO_SEPARATED, "");
		}
	}

	@Override
	public String getMiscInfoSeparated() {

		return getHeaderData(MISC_INFO_SEPARATED);
	}

	@Override
	public void setMiscInfoSeparated(String miscInfoSeparated) {

		putHeaderData(MISC_INFO_SEPARATED, miscInfoSeparated);
	}

	@Override
	public String getShortInfo() {

		return getHeaderData(SHORT_INFO);
	}

	@Override
	public void setShortInfo(String shortInfo) {

		if(shortInfo != null) {
			putHeaderData(SHORT_INFO, shortInfo);
		} else {
			putHeaderData(SHORT_INFO, "");
		}
	}

	@Override
	public String getDetailedInfo() {

		return getHeaderData(DETAILED_INFO);
	}

	@Override
	public void setDetailedInfo(String detailedInfo) {

		if(detailedInfo != null) {
			putHeaderData(DETAILED_INFO, detailedInfo);
		} else {
			putHeaderData(DETAILED_INFO, "");
		}
	}

	@Override
	public String getSampleGroup() {

		return getHeaderData(SAMPLE_GROUP);
	}

	@Override
	public void setSampleGroup(String sampleGroup) {

		if(sampleGroup != null) {
			putHeaderData(SAMPLE_GROUP, sampleGroup);
		} else {
			putHeaderData(SAMPLE_GROUP, "");
		}
	}

	@Override
	public String getBarcode() {

		return getHeaderData(BARCODE);
	}

	@Override
	public void setBarcode(String barcode) {

		if(barcode != null) {
			putHeaderData(BARCODE, barcode);
		} else {
			putHeaderData(BARCODE, "");
		}
	}

	@Override
	public String getBarcodeType() {

		return getHeaderData(BARCODE_TYPE);
	}

	@Override
	public void setBarcodeType(String barcodeType) {

		if(barcodeType != null) {
			putHeaderData(BARCODE_TYPE, barcodeType);
		} else {
			putHeaderData(BARCODE_TYPE, "");
		}
	}

	@Override
	public double getSampleWeight() {

		try {
			return Double.parseDouble(getHeaderData(SAMPLE_WEIGHT));
		} catch(Exception e) {
			return 0.0f;
		}
	}

	@Override
	public void setSampleWeight(double sampleWeight) {

		if(sampleWeight >= 0) {
			headerDataMap.put(SAMPLE_WEIGHT, Double.valueOf(sampleWeight).toString());
		} else {
			headerDataMap.put(SAMPLE_WEIGHT, Double.valueOf(0.0d).toString());
		}
	}

	@Override
	public String getSampleWeightUnit() {

		return getHeaderData(SAMPLE_WEIGHT_UNIT);
	}

	@Override
	public void setSampleWeightUnit(String sampleWeightUnit) {

		if(sampleWeightUnit != null) {
			putHeaderData(SAMPLE_WEIGHT_UNIT, sampleWeightUnit);
		} else {
			putHeaderData(SAMPLE_WEIGHT_UNIT, "");
		}
	}

	@Override
	public String getDataName() {

		return getHeaderData(DATA_NAME);
	}

	@Override
	public void setDataName(String dataName) {

		if(dataName != null) {
			putHeaderData(DATA_NAME, dataName);
		} else {
			putHeaderData(DATA_NAME, "");
		}
	}
}
