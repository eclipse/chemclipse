/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.purity;

import org.eclipse.chemclipse.chromatogram.msd.comparison.exceptions.ComparisonException;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class MassSpectrumPurityResult extends AbstractMassSpectrumPurityResult {

	/**
	 * The fit value returns how good the extracted mass spectrum matches the
	 * genuine mass spectrum.<br/>
	 * The reverse fit value returns the percentage match of the
	 * genuineMassSpectrum to the extractedMassSpectrum.
	 * 
	 * @param extractedMassSpectrum
	 * @param genuineMassSpectrum
	 * @throws ComparisonException
	 */
	public MassSpectrumPurityResult(IScanMSD extractedMassSpectrum, IScanMSD genuineMassSpectrum) throws ComparisonException {
		super(extractedMassSpectrum, genuineMassSpectrum);
	}
}
