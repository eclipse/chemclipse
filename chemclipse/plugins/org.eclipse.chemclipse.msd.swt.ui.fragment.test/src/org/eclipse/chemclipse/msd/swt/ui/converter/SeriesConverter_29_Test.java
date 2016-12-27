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
package org.eclipse.chemclipse.msd.swt.ui.converter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.support.IOffset;
import org.eclipse.chemclipse.swt.ui.support.Offset;
import org.eclipse.chemclipse.swt.ui.support.Sign;

/**
 * Tests SeriesConverter.convertPeak(peak, includeBackground, sign)
 * 
 * @author eselmeister
 */
public class SeriesConverter_29_Test extends PeakTestCase {

	private IMultipleSeries series;
	private List<IPeakMSD> peaks;
	private IOffset offset;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		peaks = new ArrayList<IPeakMSD>();
		peaks.add(getPeak());
		peaks.add(getPeak());
		offset = new Offset(500, 10000);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		peaks = null;
		series = null;
	}

	public void testConvertPeak_1() {

		series = SeriesConverter.convertPeaks(peaks, false, Sign.NEGATIVE, offset);
		assertEquals("XMin", 1000.0d, series.getXMin());
	}

	public void testConvertPeak_2() {

		series = SeriesConverter.convertPeaks(peaks, false, Sign.NEGATIVE, offset);
		assertEquals("XMax", 15500.0d, series.getXMax());
	}

	public void testConvertPeak_3() {

		series = SeriesConverter.convertPeaks(peaks, false, Sign.NEGATIVE, offset);
		assertEquals("YMin", -15231.0d, series.getYMin());
	}

	public void testConvertPeak_4() {

		/*
		 * 5231 + 10000 offset
		 */
		series = SeriesConverter.convertPeaks(peaks, false, Sign.NEGATIVE, offset);
		assertEquals("YMax", -0.0d, series.getYMax());
	}
}
