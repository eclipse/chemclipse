/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Philip Wenig - reverted to version from 2018 ... the map must not be replaced by other map!
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;

public interface IMeasurementInfo extends Serializable {

	boolean isKeyProtected(String key);

	String getHeaderData(String key);

	String getHeaderDataOrDefault(String key, String defaultValue);

	boolean headerDataContainsKey(String key);

	void putHeaderData(String key, String value);

	void putHeaderData(Map<String, String> headerData);

	/**
	 * The key must not be protected.
	 * 
	 * @param key
	 * @throws InvalidHeaderModificationException
	 */
	void removeHeaderData(String key) throws InvalidHeaderModificationException;

	/**
	 * This map is unmodifiable. Please use the
	 * setValue method to add values.
	 * 
	 * @return Map<String, String>
	 */
	Map<String, String> getHeaderDataMap();

	String getOperator();

	void setOperator(String operator);

	Date getDate();

	void setDate(Date date);

	String getMiscInfo();

	void setMiscInfo(String miscInfo);

	String getMiscInfoSeparated();

	void setMiscInfoSeparated(String miscInfoSeparated);

	String getShortInfo();

	void setShortInfo(String shortInfo);

	String getDetailedInfo();

	void setDetailedInfo(String detailedInfo);

	String getSampleGroup();

	void setSampleGroup(String sampleGroup);

	String getBarcode();

	void setBarcode(String barcode);

	String getBarcodeType();

	void setBarcodeType(String barcodeType);

	double getSampleWeight();

	void setSampleWeight(double sampleWeight);

	String getSampleWeightUnit();

	void setSampleWeightUnit(String sampleWeightUnit);

	String getDataName();

	void setDataName(String dataName);
}
