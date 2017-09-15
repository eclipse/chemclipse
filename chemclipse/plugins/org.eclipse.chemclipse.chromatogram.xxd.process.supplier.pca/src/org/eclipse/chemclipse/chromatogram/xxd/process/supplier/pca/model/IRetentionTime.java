/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
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

import java.util.ArrayList;
import java.util.List;

public interface IRetentionTime extends Comparable<IRetentionTime> {

	static List<IRetentionTime> create(List<Integer> retentionTimes) {

		List<IRetentionTime> retentionTimesList = new ArrayList<>();
		for(int i = 0; i < retentionTimes.size(); i++) {
			retentionTimesList.add(new RetentionTime(retentionTimes.get(i)));
		}
		return retentionTimesList;
	}

	@Override
	default int compareTo(IRetentionTime o) {

		return Integer.compare(getRetentionTime(), o.getRetentionTime());
	}

	String getDescription();

	int getRetentionTime();

	double getRetentionTimeMinutes();

	boolean isSelected();

	void setDescription(String description);

	void setSelected(boolean selected);
}
