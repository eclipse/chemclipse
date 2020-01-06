/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.osgi.service.component.annotations.Component;

@Component(service = IProcessTypeSupplier.class)
public class ScanMassspectrumFilterProcessTypeSupplier extends AbstractMassspectrumFilterProcessTypeSupplier {

	public ScanMassspectrumFilterProcessTypeSupplier() {
		super("Scan Massspectrum Filter", "mzfilter.msd.scan.", new Function<IChromatogramSelection<?, ?>, List<IScanMSD>>() {

			@Override
			public List<IScanMSD> apply(IChromatogramSelection<?, ?> chromatogramSelection) {

				List<IScanMSD> massspectras = new ArrayList<>();
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
				int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
				for(int scanIndex = startScan; scanIndex <= stopScan; scanIndex++) {
					IScan scan = chromatogram.getScan(scanIndex);
					if(scan instanceof IScanMSD) {
						massspectras.add((IScanMSD)scan);
					}
				}
				return massspectras;
			}
		});
	}
}
