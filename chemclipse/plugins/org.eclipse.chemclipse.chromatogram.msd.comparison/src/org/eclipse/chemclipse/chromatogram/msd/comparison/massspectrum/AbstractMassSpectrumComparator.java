/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractMassSpectrumComparator implements IMassSpectrumComparator {

	private static final String DESCRIPTION = "MassSpectrum Comparator";
	private static final Logger logger = Logger.getLogger(AbstractMassSpectrumComparator.class);
	//
	private IMassSpectrumComparisonSupplier massSpectrumComparisonSupplier;

	@Override
	public IProcessingInfo<IComparisonResult> validate(final IScanMSD unknown, final IScanMSD reference) {

		String msg = null;
		final IProcessingInfo<IComparisonResult> processingInfo = new ProcessingInfo<>();
		if(unknown == null) {
			msg = "The unknown mass spectum does not exists.";
			logger.error(msg);
			processingInfo.addErrorMessage(DESCRIPTION, msg);
		} else {
			if(unknown.getIons().isEmpty()) {
				msg = "There is no ion in the unknown mass spectum.";
				logger.error(msg);
				processingInfo.addErrorMessage(DESCRIPTION, msg);
			}
		}
		if(reference == null) {
			msg = "The reference mass spectum does not exists.";
			logger.error(msg);
			processingInfo.addErrorMessage(DESCRIPTION, msg);
		} else {
			if(reference.getIons().isEmpty()) {
				msg = "There is no ion in the reference mass spectum.";
				logger.error(msg);
				processingInfo.addErrorMessage(DESCRIPTION, msg);
			}
		}
		return processingInfo;
	}

	protected void setMassSpectrumComparisonSupplier(IMassSpectrumComparisonSupplier massSpectrumComparisonSupplier) {

		this.massSpectrumComparisonSupplier = massSpectrumComparisonSupplier;
	}

	@Override
	public IMassSpectrumComparisonSupplier getMassSpectrumComparisonSupplier() {

		return massSpectrumComparisonSupplier;
	}
}
