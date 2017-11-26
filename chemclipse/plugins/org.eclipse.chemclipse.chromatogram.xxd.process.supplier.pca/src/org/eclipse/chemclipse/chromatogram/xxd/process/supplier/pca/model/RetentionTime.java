/*******************************************************************************
 * Copyright (c) 2017 Jan Holy
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;

public class RetentionTime implements IRetentionTime {

	public static List<RetentionTime> create(List<Integer> retentionTimes) {

		List<RetentionTime> retentionTimesList = new ArrayList<>();
		for(int i = 0; i < retentionTimes.size(); i++) {
			retentionTimesList.add(new RetentionTime(retentionTimes.get(i)));
		}
		return retentionTimesList;
	}

	/*
	 * static List<IRetentionTime> copy(List<IRetentionTime> retentionTimes) {
	 * List<IRetentionTime> newRetentionTimes = new ArrayList<>();
	 * for(int i = 0; i < retentionTimes.size(); i++) {
	 * IRetentionTime retentionTime = retentionTimes.get(i);
	 * IRetentionTime newRetentionTime = new RetentionTime(retentionTime.getRetentionTime(), retentionTime.getDescription());
	 * newRetentionTime.setSelected(retentionTime.isSelected());
	 * newRetentionTimes.add(newRetentionTime);
	 * }
	 * return newRetentionTimes;
	 * }
	 */
	private String description;
	private boolean isSelected;
	private NumberFormat nf = NumberFormat.getInstance(Locale.US);
	private int retentioTime;

	public RetentionTime(int retentioTime) {
		this.retentioTime = retentioTime;
		isSelected = true;
	}

	public RetentionTime(int retentioTime, String description) {
		this(retentioTime);
		this.description = description;
	}

	@Override
	public int compareTo(IVariable o) {

		if(o instanceof IRetentionTime) {
			IRetentionTime retentionTime = (IRetentionTime)o;
			return Integer.compare(getRetentionTime(), retentionTime.getRetentionTime());
		}
		return 0;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public Object getObject() {

		return this;
	}

	@Override
	public int getRetentionTime() {

		return retentioTime;
	}

	@Override
	public double getRetentionTimeMinutes() {

		return retentioTime / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
	}

	@Override
	public String getValue() {

		return nf.format(getRetentionTimeMinutes());
	}

	@Override
	public boolean isSelected() {

		return isSelected;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public void setSelected(boolean selected) {

		this.isSelected = selected;
	}
}
