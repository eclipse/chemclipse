/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractMassSpectrumComparator implements IMassSpectrumComparator {

	private static final String DESCRIPTION = "MassSpectrum Comparator";

	public IProcessingInfo validate(IScanMSD unknown, IScanMSD reference) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		if(unknown == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The unknown mass spectum does not exists.");
		} else {
			if(unknown.getIons().size() == 0) {
				processingInfo.addErrorMessage(DESCRIPTION, "There is no ion in the unknown mass spectum.");
			}
		}
		if(reference == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The reference mass spectum does not exists.");
		} else {
			if(reference.getIons().size() == 0) {
				processingInfo.addErrorMessage(DESCRIPTION, "There is no ion in the reference mass spectum.");
			}
		}
		return processingInfo;
	}
}
