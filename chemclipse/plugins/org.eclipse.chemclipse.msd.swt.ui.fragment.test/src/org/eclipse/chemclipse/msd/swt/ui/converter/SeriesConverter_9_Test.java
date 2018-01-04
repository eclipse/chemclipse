/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Sign;

/**
 * Tests SeriesConverter.convertChromatogram(chromatogramSelection, Sign sign)
 * 
 * @author eselmeister
 */
public class SeriesConverter_9_Test extends ChromatogramSelectionTestCase {

	private IChromatogramSelectionMSD chromatogramSelection;
	private ISeries series;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		this.chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
			ChromatogramSelectionMSD selection = (ChromatogramSelectionMSD)chromatogramSelection;
			selection.setStartRetentionTime(6000);
			selection.setStopRetentionTime(7000);
		}
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		chromatogramSelection = null;
		series = null;
	}

	public void testChromatogramSelection_1() {

		assertEquals("Start Retention Time", 6000, chromatogramSelection.getStartRetentionTime());
	}

	public void testChromatogramSelection_2() {

		assertEquals("Stop Retention Time", 7000, chromatogramSelection.getStopRetentionTime());
	}

	public void testConvertChromatogramOverview_1() {

		series = SeriesConverter.convertChromatogram(chromatogramSelection, Sign.POSITIVE, true);
		assertEquals("XMin", 6000.0d, series.getXMin());
	}

	public void testConvertChromatogramOverview_2() {

		series = SeriesConverter.convertChromatogram(chromatogramSelection, Sign.POSITIVE, true);
		assertEquals("XMax", 7000.0d, series.getXMax());
	}

	public void testConvertChromatogramOverview_3() {

		series = SeriesConverter.convertChromatogram(chromatogramSelection, Sign.POSITIVE, true);
		assertEquals("YMin", 66200.0d, series.getYMin());
	}

	public void testConvertChromatogramOverview_4() {

		series = SeriesConverter.convertChromatogram(chromatogramSelection, Sign.POSITIVE, true);
		assertEquals("YMax", 1761450.0d, series.getYMax());
	}
}
