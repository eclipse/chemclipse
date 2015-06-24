/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.series;

import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.MultipleSeries;

import junit.framework.TestCase;

public class MultipleSeries_3_Test extends TestCase {

	private IMultipleSeries multipleSeries;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		multipleSeries = new MultipleSeries();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		multipleSeries = null;
	}

	public void testMultipleSeries_1() {

		assertEquals("Size", 0, multipleSeries.getMultipleSeries().size());
	}

	public void testMultipleSeries_2() {

		assertEquals("XMin", 0.0d, multipleSeries.getXMin());
	}

	public void testMultipleSeries_3() {

		assertEquals("XMax", 0.0d, multipleSeries.getXMax());
	}

	public void testMultipleSeries_4() {

		assertEquals("YMin", 0.0d, multipleSeries.getYMin());
	}

	public void testMultipleSeries_5() {

		assertEquals("YMax", 0.0d, multipleSeries.getYMax());
	}
}
