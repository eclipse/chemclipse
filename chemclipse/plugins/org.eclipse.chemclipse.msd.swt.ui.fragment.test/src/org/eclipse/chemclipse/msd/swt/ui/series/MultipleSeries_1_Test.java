/*******************************************************************************
 * Copyright (c) 2008, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.series;

import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.MultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.Series;

import junit.framework.TestCase;

public class MultipleSeries_1_Test extends TestCase {

	private ISeries series;
	private double[] xSeries;
	private double[] ySeries;
	private String id;
	private IMultipleSeries multipleSeries;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		multipleSeries = new MultipleSeries();
		// ------------------------------
		xSeries = new double[]{5, 7, 2, 9, 3, 4, 2, 4, 3, 2};
		ySeries = new double[]{84, 27, 3, 492, 38, 3, 12, 4, 54, 1};
		id = "FirstId";
		series = new Series(xSeries, ySeries, id);
		multipleSeries.add(series);
		// ------------------------------
		// ------------------------------
		xSeries = new double[]{0.5, 1, 2, 2, 4, 4, 3, 1};
		ySeries = new double[]{2, 36, 4856, 3, 12, 1457, 54, 2};
		id = "SecondId";
		series = new Series(xSeries, ySeries, id);
		multipleSeries.add(series);
		// ------------------------------
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		multipleSeries = null;
	}

	public void testMultipleSeries_1() {

		assertEquals("Size", 2, multipleSeries.getMultipleSeries().size());
	}

	public void testMultipleSeries_2() {

		assertEquals("XMin", 0.5d, multipleSeries.getXMin());
	}

	public void testMultipleSeries_3() {

		assertEquals("XMax", 9.0d, multipleSeries.getXMax());
	}

	public void testMultipleSeries_4() {

		assertEquals("YMin", 1.0d, multipleSeries.getYMin());
	}

	public void testMultipleSeries_5() {

		assertEquals("YMax", 4856.0d, multipleSeries.getYMax());
	}
}
