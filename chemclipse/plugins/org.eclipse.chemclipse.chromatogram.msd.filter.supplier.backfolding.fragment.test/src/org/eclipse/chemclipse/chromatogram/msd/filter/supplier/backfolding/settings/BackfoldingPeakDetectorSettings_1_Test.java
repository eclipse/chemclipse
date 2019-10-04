/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings;

import junit.framework.TestCase;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.Threshold;

public class BackfoldingPeakDetectorSettings_1_Test extends TestCase {

	private PeakDetectorSettings settings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		settings = new PeakDetectorSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		settings = null;
		super.tearDown();
	}

	public void testGetBackfoldingSettings_1() {

		IBackfoldingSettings backfoldingSettings = settings.getBackfoldingSettings();
		assertNotNull(backfoldingSettings);
	}

	public void testGetThreshold_1() {

		Threshold threshold = settings.getThreshold();
		assertEquals("Threshold", Threshold.MEDIUM, threshold);
	}

	public void testSetThreshold_1() {

		settings.setThreshold(Threshold.HIGH);
		Threshold threshold = settings.getThreshold();
		assertEquals("Threshold", Threshold.HIGH, threshold);
	}
}
