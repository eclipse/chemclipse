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
package org.eclipse.chemclipse.msd.swt.ui.converter;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Sign;

/**
 * Tests SeriesConverter.convertChromatogram(chromatogramSelection, Sign sign)
 * 
 * @author eselmeister
 */
public class SeriesConverter_32_Test extends ChromatogramSelectionTestCase {

	private IChromatogramSelectionMSD chromatogramSelection;
	private IChromatogramMSD chromatogram;
	private ISeries series;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogramSelection = getChromatogramSelection();
		chromatogram = chromatogramSelection.getChromatogramMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		chromatogramSelection = null;
		chromatogram = null;
		series = null;
	}

	public void testConvertChromatogramOverview_1() {

		series = SeriesConverter.convertChromatogram(chromatogram, Sign.NEGATIVE, true);
		assertEquals("XMin", 5000.0d, series.getXMin());
	}

	public void testConvertChromatogramOverview_2() {

		series = SeriesConverter.convertChromatogram(chromatogram, Sign.NEGATIVE, true);
		assertEquals("XMax", 8000.0d, series.getXMax());
	}

	public void testConvertChromatogramOverview_3() {

		series = SeriesConverter.convertChromatogram(chromatogram, Sign.NEGATIVE, true);
		assertEquals("YMin", -1761450.0d, series.getYMin());
	}

	public void testConvertChromatogramOverview_4() {

		series = SeriesConverter.convertChromatogram(chromatogram, Sign.NEGATIVE, true);
		assertEquals("YMax", -22000.0d, series.getYMax());
	}
}
