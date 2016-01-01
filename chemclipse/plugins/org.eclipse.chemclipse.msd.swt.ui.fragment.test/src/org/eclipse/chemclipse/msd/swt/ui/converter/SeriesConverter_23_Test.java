/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.converter;

import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Sign;

/**
 * Tests SeriesConverter.convertPeak(peak, includeBackground, sign)
 * 
 * @author eselmeister
 */
public class SeriesConverter_23_Test extends PeakTestCase {

	private ISeries series;
	private IChromatogramPeakMSD peak;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		this.peak = getPeak();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		peak = null;
		series = null;
	}

	public void testPeak_1() {

		assertNotNull(peak);
	}

	public void testConvertPeak_1() {

		series = SeriesConverterMSD.convertPeak(peak, false, Sign.POSITIVE);
		assertEquals("XMin", 1500.0d, series.getXMin());
	}

	public void testConvertPeak_2() {

		series = SeriesConverterMSD.convertPeak(peak, false, Sign.POSITIVE);
		assertEquals("XMax", 15500.0d, series.getXMax());
	}

	public void testConvertPeak_3() {

		series = SeriesConverterMSD.convertPeak(peak, false, Sign.POSITIVE);
		assertEquals("YMin", 0.0d, series.getYMin());
	}

	public void testConvertPeak_4() {

		series = SeriesConverterMSD.convertPeak(peak, false, Sign.POSITIVE);
		assertEquals("YMax", 5231.0d, series.getYMax());
	}
}
