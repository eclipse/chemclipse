/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Sign;

/**
 * Tests SeriesConverter.convertChromatogramOverview
 * 
 * @author eselmeister
 */
public class SeriesConverter_3_Test extends ChromatogramOverviewTestCase {

	private IChromatogramOverview chromatogramOverview;
	private ISeries series;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogramOverview = getChromatogramOverview();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testConvertChromatogramOverview_1() {

		series = SeriesConverter.convertChromatogramOverview(chromatogramOverview, Sign.POSITIVE, true);
		assertEquals("XMin", 5000.0d, series.getXMin());
	}

	public void testConvertChromatogramOverview_2() {

		series = SeriesConverter.convertChromatogramOverview(chromatogramOverview, Sign.POSITIVE, true);
		assertEquals("XMax", 8000.0d, series.getXMax());
	}

	public void testConvertChromatogramOverview_3() {

		series = SeriesConverter.convertChromatogramOverview(chromatogramOverview, Sign.POSITIVE, true);
		assertEquals("YMin", 22000.0d, series.getYMin());
	}

	public void testConvertChromatogramOverview_4() {

		series = SeriesConverter.convertChromatogramOverview(chromatogramOverview, Sign.POSITIVE, true);
		assertEquals("YMax", 1712850.0d, series.getYMax());
	}

	public void testConvertChromatogramOverview_5() {

		series = SeriesConverter.convertChromatogramOverview(chromatogramOverview, Sign.POSITIVE, true);
		double[] xSeries = series.getXSeries();
		assertEquals("xSeries", 4, xSeries.length);
	}

	public void testConvertChromatogramOverview_6() {

		series = SeriesConverter.convertChromatogramOverview(chromatogramOverview, Sign.POSITIVE, true);
		double[] ySeries = series.getYSeries();
		assertEquals("ySeries", 4, ySeries.length);
	}

	public void testConvertChromatogramOverview_7() {

		series = SeriesConverter.convertChromatogramOverview(chromatogramOverview, Sign.POSITIVE, true);
		assertNotNull(series.getId());
	}
}
