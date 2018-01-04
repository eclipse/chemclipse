/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractPeakFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class SubtractFilterSettingsPeak extends AbstractPeakFilterSettings implements ISubtractFilterSettingsPeak {

	private IScanMSD massSpectrum;
	private boolean useNominalMasses;
	private boolean useNormalize;

	@Override
	public IScanMSD getSubtractMassSpectrum() {

		return massSpectrum;
	}

	@Override
	public void setSubtractMassSpectrum(IScanMSD massSpectrum) {

		this.massSpectrum = massSpectrum;
	}

	@Override
	public boolean isUseNominalMasses() {

		return useNominalMasses;
	}

	@Override
	public void setUseNominalMasses(boolean useNominalMasses) {

		this.useNominalMasses = useNominalMasses;
	}

	@Override
	public boolean isNormalize() {

		return useNormalize;
	}

	@Override
	public void setUseNormalize(boolean useNormalize) {

		this.useNormalize = useNormalize;
	}
}
