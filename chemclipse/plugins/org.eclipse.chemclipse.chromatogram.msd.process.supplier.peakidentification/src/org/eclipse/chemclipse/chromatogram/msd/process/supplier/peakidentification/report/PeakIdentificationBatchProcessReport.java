/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.report;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.implementation.Peaks;

public class PeakIdentificationBatchProcessReport implements IPeakIdentificationBatchProcessReport {

	private IPeaks peaks;

	public PeakIdentificationBatchProcessReport() {
		peaks = new Peaks();
	}

	@Override
	public IPeaks getPeaks() {

		return peaks;
	}
}
