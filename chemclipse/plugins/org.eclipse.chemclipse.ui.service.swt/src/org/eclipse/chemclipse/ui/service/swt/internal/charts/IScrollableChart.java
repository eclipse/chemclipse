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

import org.eclipse.chemclipse.ui.service.swt.core.IChartSettings;
import org.eclipse.chemclipse.ui.service.swt.exceptions.SeriesException;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;

public interface IScrollableChart {

	void applySettings(IChartSettings chartSettings);

	BaseChart getBaseChart();

	ISeries createSeries(SeriesType seriesType, double[] xSeries, double[] ySeries, String id) throws SeriesException;

	void deleteSeries(String id);

	void adjustRange(boolean adjustMinMax);
}
