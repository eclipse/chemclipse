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
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.model.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class RetentionTime extends AbstractVariable implements IRetentionTime {

	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
	private int retentionTime = 0;

	public static List<RetentionTime> create(List<Integer> retentionTimes) {

		List<RetentionTime> retentionTimesList = new ArrayList<>();
		for(int i = 0; i < retentionTimes.size(); i++) {
			retentionTimesList.add(new RetentionTime(retentionTimes.get(i)));
		}
		return retentionTimesList;
	}

	public RetentionTime(int retentionTime) {

		super();
		this.retentionTime = retentionTime;
		setValue(convertValue());
		setType(IRetentionTime.TYPE);
		setSelected(true);
	}

	public RetentionTime(int retentionTime, String description) {

		this(retentionTime);
		setDescription(description);
	}

	@Override
	public int compareTo(IVariable o) {

		if(o instanceof IRetentionTime) {
			IRetentionTime retentionTime = (IRetentionTime)o;
			return Integer.compare(getRetentionTime(), retentionTime.getRetentionTime());
		}
		return 0;
	}

	private String convertValue() {

		return decimalFormat.format(getRetentionTimeMinutes());
	}

	@Override
	public int getRetentionTime() {

		return retentionTime;
	}

	@Override
	public double getRetentionTimeMinutes() {

		return retentionTime / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
	}

	@Override
	public void setRetentionTime(int retentionTime) {

		this.retentionTime = retentionTime;
		setValue(convertValue());
	}
}
