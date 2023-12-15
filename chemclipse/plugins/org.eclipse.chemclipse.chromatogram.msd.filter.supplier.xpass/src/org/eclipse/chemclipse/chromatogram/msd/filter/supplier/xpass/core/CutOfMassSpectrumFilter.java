/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.AbstractMassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.result.IMassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings.CutOfMassSpectrumFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class CutOfMassSpectrumFilter extends AbstractMassSpectrumFilter {

	public CutOfMassSpectrumFilter() {

	}

	@Override
	public IProcessingInfo<IMassSpectrumFilterResult> applyFilter(List<IScanMSD> massSpectra, IMassSpectrumFilterSettings massSpectrumFilterSettings, IProgressMonitor monitor) {

		CutOfMassSpectrumFilterSettings settings;
		if(massSpectrumFilterSettings instanceof CutOfMassSpectrumFilterSettings cutOfMassSpectrumFilterSettings) {
			settings = cutOfMassSpectrumFilterSettings;
		} else {
			settings = new CutOfMassSpectrumFilterSettings();
		}
		for(IScanMSD massSpectrum : massSpectra) {
			List<IIon> ions = new ArrayList<>(massSpectrum.getIons());
			for(IIon ion : ions) {
				if(ion.getAbundance() < settings.getThreshold()) {
					massSpectrum.removeIon(ion);
					massSpectrum.setDirty(true);
				}
			}
		}
		return null;
	}
}
