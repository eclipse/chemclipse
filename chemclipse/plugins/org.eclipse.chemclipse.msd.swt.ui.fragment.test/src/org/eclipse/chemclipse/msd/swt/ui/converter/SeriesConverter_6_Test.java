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

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.support.IOffset;
import org.eclipse.chemclipse.swt.ui.support.Offset;
import org.eclipse.chemclipse.swt.ui.support.Sign;

/**
 * Tests SeriesConverter.convertChromatogramOverviews
 * 
 * @author eselmeister
 */
public class SeriesConverter_6_Test extends ChromatogramOverviewsTestCase {

	private List<IChromatogramOverview> chromatogramOverviews;
	private IMultipleSeries multipleSeries;
	private IOffset offset;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogramOverviews = new ArrayList<IChromatogramOverview>();
		chromatogramOverviews.add(getChromatogramOverview1());
		chromatogramOverviews.add(getChromatogramOverview2());
		offset = new Offset(1200, 50000);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testConvertChromatogramOverview_1() {

		multipleSeries = SeriesConverter.convertChromatogramOverviews(chromatogramOverviews, Sign.POSITIVE, offset, true);
		assertEquals("XMin", 4200.0d, multipleSeries.getXMin());
	}

	public void testConvertChromatogramOverview_2() {

		multipleSeries = SeriesConverter.convertChromatogramOverviews(chromatogramOverviews, Sign.POSITIVE, offset, true);
		assertEquals("XMax", 8700.0d, multipleSeries.getXMax());
	}

	public void testConvertChromatogramOverview_3() {

		multipleSeries = SeriesConverter.convertChromatogramOverviews(chromatogramOverviews, Sign.POSITIVE, offset, true);
		assertEquals("YMin", 22000.0d, multipleSeries.getYMin());
	}

	public void testConvertChromatogramOverview_4() {

		multipleSeries = SeriesConverter.convertChromatogramOverviews(chromatogramOverviews, Sign.POSITIVE, offset, true);
		assertEquals("YMax", 1712850.0d, multipleSeries.getYMax());
	}
}
