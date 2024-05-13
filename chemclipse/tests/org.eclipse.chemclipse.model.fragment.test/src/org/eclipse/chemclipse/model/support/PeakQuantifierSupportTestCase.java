/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.implementation.Peak;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.PeakModel;
import org.eclipse.chemclipse.model.implementation.Scan;

import junit.framework.TestCase;

public class PeakQuantifierSupportTestCase extends TestCase {

	private IPeak peak = null;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		peak = createPeak();
	}

	@Override
	protected void tearDown() throws Exception {

		peak = null;
		super.tearDown();
	}

	public IPeak getPeak() {

		return peak;
	}

	public void test1() {

		assertNotNull(peak);
	}

	private IPeak createPeak() {

		IScan peakMaximum = new Scan(1000);
		IPeakIntensityValues peakIntensityValues = new PeakIntensityValues();
		peakIntensityValues.addIntensityValue(100, 10.f);
		peakIntensityValues.addIntensityValue(200, 30.f);
		peakIntensityValues.addIntensityValue(300, 80.f);
		peakIntensityValues.addIntensityValue(400, 100.f);
		peakIntensityValues.addIntensityValue(500, 70.f);
		peakIntensityValues.addIntensityValue(600, 40.f);
		IPeakModel peakModel = new PeakModel(peakMaximum, peakIntensityValues, 0.0f, 0.0f);
		//
		return new Peak(peakModel);
	}
}