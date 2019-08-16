/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.ui.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * This class is responsible for representing chromatogram values in the
 * properties view.
 * 
 * @author eselmeister
 */
public abstract class ChromatogramPropertySource implements IPropertySource {

	/*
	 * Categories
	 */
	private static final String CATEGORY = "File";
	private static final String RECORDS = "Records";
	/*
	 * Properties
	 */
	private static final String NAME = "name";
	private static final String SIZE = "size";
	private static final String FILE = "file";
	private static final String OPERATOR = "operator";
	private static final String DATE = "date";
	private static final String SCAN_DELAY = "scanDelay";
	private static final String SCAN_INTERVAL = "scanInterval";
	private static final String NUMBER_OF_SCANS = "numberOfScans";
	private static final String STOP_RETENTION_TIME = "stopRetentionTime";
	private static final String START_RETENTION_TIME = "startRetentionTime";
	// private static final String MAX_SIGNAL = "maxSignal";
	// private static final String MIN_SIGNAL = "minSignal";
	private final IChromatogramMSD chromatogram;

	public ChromatogramPropertySource(IChromatogramMSD chromatogram) {
		this.chromatogram = chromatogram;
	}

	@Override
	public Object getEditableValue() {

		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {

		TextPropertyDescriptor textPropertyDescriptor;
		ArrayList<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor>();
		//
		// textPropertyDescriptor = new TextPropertyDescriptor(MIN_SIGNAL, "Min Signal");
		// textPropertyDescriptor.setCategory(RECORDS);
		// list.add(textPropertyDescriptor);
		//
		// textPropertyDescriptor = new TextPropertyDescriptor(MAX_SIGNAL, "Max Signal");
		// textPropertyDescriptor.setCategory(RECORDS);
		// list.add(textPropertyDescriptor);
		//
		textPropertyDescriptor = new TextPropertyDescriptor(START_RETENTION_TIME, "Start Retention Time (Minutes)");
		textPropertyDescriptor.setCategory(RECORDS);
		list.add(textPropertyDescriptor);
		//
		textPropertyDescriptor = new TextPropertyDescriptor(STOP_RETENTION_TIME, "Stop Retention Time (Minutes)");
		textPropertyDescriptor.setCategory(RECORDS);
		list.add(textPropertyDescriptor);
		//
		textPropertyDescriptor = new TextPropertyDescriptor(NUMBER_OF_SCANS, "Scans");
		textPropertyDescriptor.setCategory(RECORDS);
		list.add(textPropertyDescriptor);
		//
		textPropertyDescriptor = new TextPropertyDescriptor(SCAN_DELAY, "Scan Delay (Milliseconds)");
		textPropertyDescriptor.setCategory(RECORDS);
		list.add(textPropertyDescriptor);
		//
		textPropertyDescriptor = new TextPropertyDescriptor(SCAN_INTERVAL, "Scan Interval (Milliseconds)");
		textPropertyDescriptor.setCategory(RECORDS);
		list.add(textPropertyDescriptor);
		//
		textPropertyDescriptor = new TextPropertyDescriptor(DATE, "Date");
		textPropertyDescriptor.setCategory(RECORDS);
		list.add(textPropertyDescriptor);
		//
		textPropertyDescriptor = new TextPropertyDescriptor(OPERATOR, "Operator");
		textPropertyDescriptor.setCategory(RECORDS);
		list.add(textPropertyDescriptor);
		//
		textPropertyDescriptor = new TextPropertyDescriptor(FILE, CATEGORY);
		textPropertyDescriptor.setCategory(CATEGORY);
		list.add(textPropertyDescriptor);
		//
		textPropertyDescriptor = new TextPropertyDescriptor(SIZE, "Size (MB)");
		textPropertyDescriptor.setCategory(CATEGORY);
		list.add(textPropertyDescriptor);
		//
		textPropertyDescriptor = new TextPropertyDescriptor(NAME, "Name");
		textPropertyDescriptor.setCategory(CATEGORY);
		list.add(textPropertyDescriptor);
		//
		return list.toArray(new IPropertyDescriptor[list.size()]);
	}

	@Override
	public Object getPropertyValue(Object id) {

		// TODO speed optimize min/max signal
		// if(MIN_SIGNAL.equals(id))
		// return chromatogram.getMinSignal();
		// else if(MAX_SIGNAL.equals(id))
		// return chromatogram.getMaxSignal();
		if(START_RETENTION_TIME.equals(id))
			return getFormattedValue((double)chromatogram.getStartRetentionTime(), IChromatogramMSD.MINUTE_CORRELATION_FACTOR, "0.00");
		else if(STOP_RETENTION_TIME.equals(id))
			return getFormattedValue((double)chromatogram.getStopRetentionTime(), IChromatogramMSD.MINUTE_CORRELATION_FACTOR, "0.00");
		else if(NUMBER_OF_SCANS.equals(id))
			return Integer.valueOf(chromatogram.getNumberOfScans()).toString();
		else if(SCAN_DELAY.equals(id))
			return Integer.valueOf(chromatogram.getScanDelay()).toString();
		else if(SCAN_INTERVAL.equals(id))
			return Integer.valueOf(chromatogram.getScanInterval()).toString();
		else if(DATE.equals(id))
			return chromatogram.getDate().toString();
		else if(OPERATOR.equals(id))
			return chromatogram.getOperator();
		else if(FILE.equals(id))
			return chromatogram.getFile().toString();
		else if(SIZE.equals(id))
			return getFormattedValue((double)chromatogram.getFile().length(), (double)(1000 * 1000), "0.00");
		else if(NAME.equals(id))
			return chromatogram.getName();
		return null;
	}

	/**
	 * Returns a formatted string. The value is divided by the denominator. If
	 * the denominator is 0 only the value will be formatted. Pattern is
	 * IFormatter description to format the value.
	 * 
	 * @param value
	 * @param denominator
	 * @param pattern
	 *            - for example "#.00"
	 * @return String
	 */
	public String getFormattedValue(double value, double denominator, String pattern) {

		DecimalFormat formatter;
		if(denominator != 0) {
			value = value / denominator;
		}
		if(pattern == null || pattern == "") {
			pattern = "#.00";
		}
		formatter = ValueFormat.getDecimalFormatEnglish(pattern);
		return formatter.format(value);
	}

	@Override
	public boolean isPropertySet(Object id) {

		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {

	}

	/**
	 * Values like minSignal must not be changed.
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {

	}
}
