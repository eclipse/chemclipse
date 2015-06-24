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
package org.eclipse.chemclipse.msd.swt.ui.converter;

import java.util.List;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.support.IOffset;
import org.eclipse.chemclipse.swt.ui.support.Offset;
import org.eclipse.chemclipse.swt.ui.support.Sign;

/**
 * Tests SeriesConverter.convertChromatogram(chromatogramSelection, Sign sign)
 * 
 * @author eselmeister
 */
public class SeriesConverter_11_Test extends ChromatogramSelectionsTestCase {

	private List<IChromatogramSelection> chromatogramSelections;
	private IMultipleSeries series;
	private IOffset offset;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		this.chromatogramSelections = getChromatogramSelections();
		this.offset = new Offset(500, 20000);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		chromatogramSelections = null;
		series = null;
		offset = null;
	}

	public void testConvertChromatogramOverview_1() {

		series = SeriesConverter.convertChromatograms(chromatogramSelections, Sign.POSITIVE, offset, true);
		assertEquals("XMin", 4500.0d, series.getXMin());
	}

	public void testConvertChromatogramOverview_2() {

		series = SeriesConverter.convertChromatograms(chromatogramSelections, Sign.POSITIVE, offset, true);
		assertEquals("XMax", 8000.0d, series.getXMax());
	}

	public void testConvertChromatogramOverview_3() {

		series = SeriesConverter.convertChromatograms(chromatogramSelections, Sign.POSITIVE, offset, true);
		assertEquals("YMin", 15000.0d, series.getYMin());
	}

	public void testConvertChromatogramOverview_4() {

		series = SeriesConverter.convertChromatograms(chromatogramSelections, Sign.POSITIVE, offset, true);
		assertEquals("YMax", 1292450.0d, series.getYMax());
	}
}
