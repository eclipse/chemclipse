/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ui.service.swt.internal.charts;

import org.swtchart.IAxis;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;

public interface IDataCoordinates {

	boolean isUseZeroY();

	void setUseZeroY(boolean useZeroY);

	boolean isUseZeroX();

	void setUseZeroX(boolean useZeroX);

	double getLengthX();

	double getLengthY();

	double getMinX();

	double getMaxX();

	double getMinY();

	double getMaxY();

	/**
	 * SeriesType.LINE or SeriesType.BAR
	 * 
	 * @param seriesType
	 * @param xSeries
	 * @param ySeries
	 * @param id
	 * @return
	 */
	ISeries createSeries(SeriesType seriesType, double[] xSeries, double[] ySeries, String id);

	/**
	 * Adjust the axis to its allowed/constrained values.
	 * 
	 * @param axis
	 */
	void adjustMinMaxRange(IAxis axis);
}