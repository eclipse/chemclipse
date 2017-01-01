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

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Sign;

/**
 * Tests SeriesConverter.convertBaseline
 * 
 * @author eselmeister
 */
public class SeriesConverter_2_Test extends ChromatogramSelectionTestCase {

	private IChromatogramSelectionMSD chromatogramSelection;
	private ISeries series;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogramSelection = getChromatogramSelection();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testConvertBaseline_1() {

		series = SeriesConverter.convertBaseline(chromatogramSelection, Sign.NEGATIVE, true);
		assertEquals("XMin", 5000.0d, series.getXMin());
	}

	public void testConvertBaseline_2() {

		series = SeriesConverter.convertBaseline(chromatogramSelection, Sign.NEGATIVE, true);
		assertEquals("XMax", 8000.0d, series.getXMax());
	}

	public void testConvertBaseline_3() {

		series = SeriesConverter.convertBaseline(chromatogramSelection, Sign.NEGATIVE, true);
		assertEquals("YMin", -500.0d, series.getYMin());
	}

	public void testConvertBaseline_4() {

		series = SeriesConverter.convertBaseline(chromatogramSelection, Sign.NEGATIVE, true);
		assertEquals("YMax", -0.0d, series.getYMax());
	}

	public void testConvertBaseline_5() {

		series = SeriesConverter.convertBaseline(chromatogramSelection, Sign.POSITIVE, true);
		double[] xSeries = series.getXSeries();
		assertEquals("xSeries", 4, xSeries.length);
	}

	public void testConvertBaseline_6() {

		series = SeriesConverter.convertBaseline(chromatogramSelection, Sign.NEGATIVE, true);
		double[] ySeries = series.getYSeries();
		assertEquals("ySeries", 4, ySeries.length);
	}

	public void testConvertBaseline_7() {

		series = SeriesConverter.convertBaseline(chromatogramSelection, Sign.NEGATIVE, true);
		assertNotNull(series.getId());
	}
}
