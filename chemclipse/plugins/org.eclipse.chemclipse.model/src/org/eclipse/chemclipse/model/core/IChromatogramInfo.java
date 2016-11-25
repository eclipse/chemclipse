/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.Date;
import java.util.Map;

public interface IChromatogramInfo {

	/**
	 * Returns the operator.
	 * 
	 * @return String
	 */
	String getOperator();

	void setOperator(String operator);

	/**
	 * Returns the date of operation.
	 * 
	 * @return Date
	 */
	Date getDate();

	void setDate(Date date);

	/**
	 * Returns miscellaneous information.
	 * 
	 * @return String
	 */
	String getMiscInfo();

	void setMiscInfo(String miscInfo);

	/**
	 * Returns separated miscellaneous information.
	 * 
	 * @return String
	 */
	String getMiscInfoSeparated();

	void setMiscInfoSeparated(String miscInfoSeparated);

	/**
	 * Returns the miscellaneous map to store various
	 * other values.
	 * 
	 * @return Map<String, String>
	 */
	Map<String, String> getMiscellaneous();

	/**
	 * Returns the short info, e.g.:
	 * Sample A
	 * 
	 * @return String
	 */
	String getShortInfo();

	void setShortInfo(String shortInfo);

	/**
	 * Returns a detailed info of the sample.
	 * 
	 * @return String
	 */
	String getDetailedInfo();

	void setDetailedInfo(String detailedInfo);

	/**
	 * Returns the sample group, e.g.:
	 * Number 192, Modification A
	 * 
	 * @return
	 */
	String getSampleGroup();

	void setSampleGroup(String sampleGroup);

	/**
	 * Returns the barcode.
	 * 
	 * @return
	 */
	String getBarcode();

	void setBarcode(String barcode);

	/**
	 * Returns the barcode type, e.g. EAN 128
	 * 
	 * @return String
	 */
	String getBarcodeType();

	void setBarcodeType(String barcodeType);

	/**
	 * Returns the sample weight, e.g. 148
	 * 
	 * @return double
	 */
	double getSampleWeight();

	void setSampleWeight(double sampleWeight);

	/**
	 * Returns the weight unit, e.g. "µg".
	 * 
	 * @return String
	 */
	String getWeightUnit();

	void setWeightUnit(String weightUnit);

	/**
	 * This is the name of the contained data, e.g. sample name.
	 * 
	 * @return String
	 */
	String getDataName();

	/**
	 * Set the contained sample name.
	 * 
	 * @param dataName
	 */
	void setDataName(String dataName);
}
