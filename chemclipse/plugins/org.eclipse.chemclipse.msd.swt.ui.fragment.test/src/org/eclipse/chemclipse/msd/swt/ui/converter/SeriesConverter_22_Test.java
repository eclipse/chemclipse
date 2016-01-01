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

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Sign;

/**
 * Tests SeriesConverter.convertChromatogramByExcludedIons(
 * chromatogramSelection, excludedIons, sign)
 * 
 * @author eselmeister
 */
public class SeriesConverter_22_Test extends XICChromatogramSelectionTestCase {

	private IChromatogramSelectionMSD chromatogramSelection;
	private ISeries series;
	private IMarkedIons selectedIons;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		this.chromatogramSelection = getChromatogramSelection();
		selectedIons = new MarkedIons();
		selectedIons.add(new MarkedIon(45));
		selectedIons.add(new MarkedIon(60));
		selectedIons.add(new MarkedIon(43));
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		chromatogramSelection = null;
		series = null;
	}

	public void testConvertChromatogramOverview_1() {

		series = SeriesConverterMSD.convertChromatogramBySelectedIons(chromatogramSelection, selectedIons, Sign.NEGATIVE);
		assertEquals("XMin", 5000.0d, series.getXMin());
	}

	public void testConvertChromatogramOverview_2() {

		series = SeriesConverterMSD.convertChromatogramBySelectedIons(chromatogramSelection, selectedIons, Sign.NEGATIVE);
		assertEquals("XMax", 8000.0d, series.getXMax());
	}

	public void testConvertChromatogramOverview_3() {

		series = SeriesConverterMSD.convertChromatogramBySelectedIons(chromatogramSelection, selectedIons, Sign.NEGATIVE);
		assertEquals("YMin", -23000.0d, series.getYMin());
	}

	public void testConvertChromatogramOverview_4() {

		series = SeriesConverterMSD.convertChromatogramBySelectedIons(chromatogramSelection, selectedIons, Sign.NEGATIVE);
		assertEquals("YMax", -0.0d, series.getYMax());
	}
}
