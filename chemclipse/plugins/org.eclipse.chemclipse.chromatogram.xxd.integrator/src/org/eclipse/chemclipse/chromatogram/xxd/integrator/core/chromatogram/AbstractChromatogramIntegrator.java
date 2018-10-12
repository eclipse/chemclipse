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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractChromatogramIntegrator implements IChromatogramIntegrator {

	private static final String DESCRIPTON = "Chromatogram Integrator";

	@SuppressWarnings("rawtypes")
	protected IProcessingInfo validate(IChromatogramSelection chromatogramSelection, IChromatogramIntegrationSettings chromatogramIntegrationSettings) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		if(chromatogramSelection == null) {
			processingInfo.addErrorMessage(DESCRIPTON, "The given chromatogram selection must not be null.");
		}
		if(chromatogramIntegrationSettings == null) {
			processingInfo.addErrorMessage(DESCRIPTON, "The given chromatogram integration settings must not be null");
		}
		return processingInfo;
	}
}
