/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.spectra;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;

public class ProblemC1 implements ITestMassSpectrum {

	private IScanMSD massSpectrum;

	public ProblemC1() throws Exception {
		massSpectrum = new ScanMSD();
		massSpectrum.addIon(new Ion(183.0d, 32.375866f));
		massSpectrum.addIon(new Ion(181.0d, 35.19116f));
		massSpectrum.addIon(new Ion(124.0d, 156.95256f));
		massSpectrum.addIon(new Ion(167.0d, 600.36115f));
		massSpectrum.addIon(new Ion(182.0d, 703.1193f));
	}

	@Override
	public IScanMSD getMassSpectrum() {

		return massSpectrum;
	}
}
