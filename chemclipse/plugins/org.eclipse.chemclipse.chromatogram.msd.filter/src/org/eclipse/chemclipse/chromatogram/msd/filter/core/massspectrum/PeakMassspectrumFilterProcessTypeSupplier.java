/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.osgi.service.component.annotations.Component;

@Component(service = IProcessTypeSupplier.class)
public class PeakMassspectrumFilterProcessTypeSupplier extends AbstractMassspectrumFilterProcessTypeSupplier {

	public PeakMassspectrumFilterProcessTypeSupplier() {
		super("Peak Massspectrum Filter", "mzfilter.msd.peak", new Function<IChromatogramSelection<?, ?>, List<IScanMSD>>() {

			@Override
			public List<IScanMSD> apply(IChromatogramSelection<?, ?> chromatogramSelection) {

				List<IScanMSD> massspectras = new ArrayList<>();
				List<?> peaks = chromatogramSelection.getChromatogram().getPeaks(chromatogramSelection);
				for(Object object : peaks) {
					if(object instanceof IPeakMSD) {
						massspectras.add(((IPeakMSD)object).getExtractedMassSpectrum());
					}
				}
				return massspectras;
			}
		});
	}
}
