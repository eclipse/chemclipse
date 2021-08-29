/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.selection;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;

import junit.framework.TestCase;

public class ChromatogramSelection_10_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IChromatogramSelectionMSD selection;
	private IChromatogramPeakMSD peak;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Use createNiceMock if you use void methods that are not important to
		 * test.
		 */
		chromatogram = EasyMock.createNiceMock(IChromatogramMSD.class);
		EasyMock.expect(chromatogram.getStartRetentionTime()).andStubReturn(1);
		EasyMock.expect(chromatogram.getStopRetentionTime()).andStubReturn(100);
		EasyMock.expect(chromatogram.getMaxSignal()).andStubReturn(127500.0f);
		EasyMock.expect(chromatogram.getNumberOfPeaks()).andStubReturn(1);
		peak = EasyMock.createNiceMock(IChromatogramPeakMSD.class);
		EasyMock.expect(peak.getIntegratedArea()).andStubReturn(893002.3d);
		EasyMock.replay(peak);
		List<IChromatogramPeakMSD> peaks = new ArrayList<IChromatogramPeakMSD>();
		peaks.add(peak);
		/*
		 * When the chromatogram selection will initialized, the first peak will
		 * be taken if available.
		 */
		EasyMock.expect(chromatogram.getPeaks()).andStubReturn(peaks);
		EasyMock.replay(chromatogram);
		/*
		 * Default values from IChromatogram will be chosen.
		 */
		selection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		selection = null;
		peak = null;
		super.tearDown();
	}

	public void testGetSelectedPeak_1() {

		IChromatogramPeakMSD selectedPeak = selection.getSelectedPeak();
		assertNotNull(selectedPeak);
		assertEquals("IntegratedArea", 893002.3d, selectedPeak.getIntegratedArea());
	}
}
