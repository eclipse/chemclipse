/*******************************************************************************
 * Copyright (c) 2008, 2016 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.Series;

import junit.framework.TestCase;

public class Series_1_Test extends TestCase {

	private ISeries series;
	private double[] xSeries;
	private double[] ySeries;
	private String id;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		xSeries = new double[]{5, 7, 2, 9, 3, 4, 2, 4, 3, 2};
		ySeries = new double[]{84, 27, 3, 492, 38, 3, 12, 4, 54, 4};
		id = "SomeId";
		series = new Series(xSeries, ySeries, id);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		xSeries = null;
		ySeries = null;
	}

	public void testXMin_1() {

		assertEquals("XMin", 2.0, series.getXMin());
	}

	public void testXMax_1() {

		assertEquals("XMax", 9.0, series.getXMax());
	}

	public void testYMin_1() {

		assertEquals("YMin", 3.0, series.getYMin());
	}

	public void testYMax_1() {

		assertEquals("YMax", 492.0, series.getYMax());
	}

	public void testId_1() {

		assertEquals("Id", "SomeId", series.getId());
	}

	public void testGetXSeries_1() {

		assertNotNull("xSeries", series.getXSeries());
	}

	public void testGetYSeries_1() {

		assertNotNull("ySeries", series.getYSeries());
	}
}
