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
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.numeric.statistics.WindowSize;

/**
 * THIS IS A TESTCLASS! ONLY USE IT FOR TEST CASES! IT'S NOT FOR PRODUCTIVE USE!
 * 
 * @author eselmeister
 */
public class PeakDetectorSettings extends AbstractPeakDetectorSettingsMSD {

	@Override
	public WindowSize getMovingAverageWindowSize() {
		return WindowSize.WIDTH_15;
	}

	@Override
	public Collection<Number> getFilterIon() {
		return Collections.emptyList();
	}

	@Override
	public FilterMode getFilterMode() {
		return FilterMode.EXCLUDE;
	}
}
