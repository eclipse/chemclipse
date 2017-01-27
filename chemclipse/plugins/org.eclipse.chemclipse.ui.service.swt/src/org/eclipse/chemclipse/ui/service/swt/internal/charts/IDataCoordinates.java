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

import org.eclipse.chemclipse.ui.service.swt.exceptions.SeriesException;
import org.swtchart.IAxis;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;

public interface IDataCoordinates {

	boolean isUseZeroY();

	void setUseZeroY(boolean useZeroY);

	boolean isUseZeroX();

	void setUseZeroX(boolean useZeroX);

	double getLength();

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
	 * @return ISeries
	 * @throws SeriesException
	 */
	ISeries createSeries(SeriesType seriesType, double[] xSeries, double[] ySeries, String id) throws SeriesException;

	/**
	 * Deletes the given series if it exists and
	 * recalculates the min/max values.
	 * 
	 * @param id
	 */
	void deleteSeries(String id);

	/**
	 * Adjusts the range of all axes and validates the min/max ranges on demand.
	 * 
	 * @param adjustMinMax
	 */
	void adjustRange(boolean adjustMinMax);

	/**
	 * Sets the range, based on the start and stop coordinates of the composite.
	 * In this case, axis.getDataCoordinate is used to get the data coordinate.
	 * 
	 * @param axis
	 * @param xStart
	 * @param xStop
	 * @param adjustMinMax
	 */
	void setRange(IAxis axis, int xStart, int xStop, boolean adjustMinMax);

	/**
	 * Sets the range, based on the start and stop coordinates of the data series.
	 * 
	 * @param axis
	 * @param xStart
	 * @param xStop
	 * @param adjustMinMax
	 */
	void setRange(IAxis axis, double start, double stop, boolean adjustMinMax);

	/**
	 * Adjust the axis to its allowed/constrained values.
	 * 
	 * @param axis
	 */
	void adjustMinMaxRange(IAxis axis);
}