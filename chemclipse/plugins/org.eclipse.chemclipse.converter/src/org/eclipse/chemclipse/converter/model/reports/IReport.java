/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.model.reports;

import java.util.List;

public interface IReport<T extends IReportPeak> {

	int getReportNumber();

	void setReportNumber(int reportNumber);

	String getName();

	void setName(String name);

	double getTotalArea();

	void setTotalArea(double totalArea);

	double getCalculatedTotalArea();

	int getNumberOfExpectedPeaks();

	void setNumberOfExpectedPeaks(int numberOfExpectedPeaks);

	List<T> getReportPeaks();

	/**
	 * This is a cross-check to validate that the
	 * data has been imported correctly.
	 * 
	 * @return boolean
	 */
	boolean isVerfied();
}