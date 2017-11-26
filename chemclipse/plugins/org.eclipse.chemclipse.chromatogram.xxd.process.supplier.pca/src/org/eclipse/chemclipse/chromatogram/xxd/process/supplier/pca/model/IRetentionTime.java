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

public interface IRetentionTime extends IVariable {

	/*
	 * static List<IRetentionTime> create(List<Integer> retentionTimes) {
	 * List<IRetentionTime> retentionTimesList = new ArrayList<>();
	 * for(int i = 0; i < retentionTimes.size(); i++) {
	 * retentionTimesList.add(new RetentionTime(retentionTimes.get(i)));
	 * }
	 * return retentionTimesList;
	 * }
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
	int getRetentionTime();

	double getRetentionTimeMinutes();
}
