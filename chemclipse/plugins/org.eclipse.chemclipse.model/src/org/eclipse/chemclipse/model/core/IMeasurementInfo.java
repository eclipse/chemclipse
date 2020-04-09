/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - extract constants into interface, add default implementations
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;
import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.text.ValueFormat;

public interface IMeasurementInfo extends Serializable {

	public static final String OPERATOR = "Operator";
	public static final String DATE = "Date";
	public static final String MISC_INFO = "Misc Info";
	public static final String MISC_INFO_SEPARATED = "Misc Info Separated";
	public static final String SHORT_INFO = "Short Info";
	public static final String DETAILED_INFO = "Detailed Info";
	public static final String SAMPLE_GROUP = "Sample Group";
	public static final String BARCODE = "Barcode";
	public static final String BARCODE_TYPE = "Barcode Type";
	public static final String SAMPLE_WEIGHT = "Sample Weight";
	public static final String SAMPLE_WEIGHT_UNIT = "Sample Weight Unit";
	public static final String DATA_NAME = "Data Name";
	public static final String X_UNIT_NAME = "X Axis Unit";
	public static final String Y_UNIT_NAME = "Y Axis Unit";
	public static final String X_AXIS_TITLE = "X Axis Title";
	public static final String Y_AXIS_TITLE = "Y Axis Title";

	default String getHeaderData(String key) {

		return getHeaderDataMap().get(key);
	}

	default String getHeaderDataOrDefault(String key, String defaultValue) {

		String data = getHeaderData(key);
		if(data == null || data.isEmpty()) {
			return defaultValue;
		}
		return data;
	}

	default boolean headerDataContainsKey(String key) {

		return getHeaderDataMap().containsKey(key);
	}

	void putHeaderData(String key, String value);

	void removeHeaderData(String key) throws InvalidHeaderModificationException;

	/**
	 * This map is unmodifiable. Please use the
	 * setValue method to add values.
	 * 
	 * @return Map<String, String>
	 */
	Map<String, String> getHeaderDataMap();

	default String getOperator() {

		return getHeaderData(OPERATOR);
	}

	default void setOperator(String operator) {

		putHeaderData(OPERATOR, operator);
	}

	default Date getDate() {

		try {
			return ValueFormat.getDateFormatEnglish(ValueFormat.FULL_DATE_PATTERN).parse(getHeaderData(DATE));
		} catch(ParseException e) {
			return new Date();
		}
	}

	default void setDate(Date date) {

		if(date != null) {
			putHeaderData(DATE, ValueFormat.getDateFormatEnglish(ValueFormat.FULL_DATE_PATTERN).format(date));
		} else {
			putHeaderData(DATE, "");
		}
	}

	default String getMiscInfo() {

		return getHeaderData(MISC_INFO);
	}

	default void setMiscInfo(String miscInfo) {

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

	default String getMiscInfoSeparated() {

		return getHeaderData(MISC_INFO_SEPARATED);
	}

	default void setMiscInfoSeparated(String miscInfoSeparated) {

		putHeaderData(MISC_INFO_SEPARATED, miscInfoSeparated);
	}

	default String getShortInfo() {

		return getHeaderData(SHORT_INFO);
	}

	default void setShortInfo(String shortInfo) {

		if(shortInfo != null) {
			putHeaderData(SHORT_INFO, shortInfo);
		} else {
			putHeaderData(SHORT_INFO, "");
		}
	}

	default String getDetailedInfo() {

		return getHeaderData(DETAILED_INFO);
	}

	default void setDetailedInfo(String detailedInfo) {

		if(detailedInfo != null) {
			putHeaderData(DETAILED_INFO, detailedInfo);
		} else {
			putHeaderData(DETAILED_INFO, "");
		}
	}

	default String getSampleGroup() {

		return getHeaderData(SAMPLE_GROUP);
	}

	default void setSampleGroup(String sampleGroup) {

		if(sampleGroup != null) {
			putHeaderData(SAMPLE_GROUP, sampleGroup);
		} else {
			putHeaderData(SAMPLE_GROUP, "");
		}
	}

	default String getBarcode() {

		return getHeaderData(BARCODE);
	}

	default void setBarcode(String barcode) {

		if(barcode != null) {
			putHeaderData(BARCODE, barcode);
		} else {
			putHeaderData(BARCODE, "");
		}
	}

	default String getBarcodeType() {

		return getHeaderData(BARCODE_TYPE);
	}

	default void setBarcodeType(String barcodeType) {

		if(barcodeType != null) {
			putHeaderData(BARCODE_TYPE, barcodeType);
		} else {
			putHeaderData(BARCODE_TYPE, "");
		}
	}

	default double getSampleWeight() {

		try {
			return Double.parseDouble(getHeaderData(SAMPLE_WEIGHT));
		} catch(Exception e) {
			return 0.0f;
		}
	}

	default void setSampleWeight(double sampleWeight) {

		if(sampleWeight >= 0) {
			putHeaderData(SAMPLE_WEIGHT, Double.valueOf(sampleWeight).toString());
		} else {
			putHeaderData(SAMPLE_WEIGHT, Double.valueOf(0.0d).toString());
		}
	}

	default String getSampleWeightUnit() {

		return getHeaderData(SAMPLE_WEIGHT_UNIT);
	}

	default void setSampleWeightUnit(String sampleWeightUnit) {

		if(sampleWeightUnit != null) {
			putHeaderData(SAMPLE_WEIGHT_UNIT, sampleWeightUnit);
		} else {
			putHeaderData(SAMPLE_WEIGHT_UNIT, "");
		}
	}

	default String getDataName() {

		return getHeaderData(DATA_NAME);
	}

	default void setDataName(String dataName) {

		if(dataName != null) {
			putHeaderData(DATA_NAME, dataName);
		} else {
			putHeaderData(DATA_NAME, "");
		}
	}
}
