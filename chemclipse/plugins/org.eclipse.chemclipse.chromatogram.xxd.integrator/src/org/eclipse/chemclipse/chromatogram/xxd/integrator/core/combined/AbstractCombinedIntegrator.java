/*******************************************************************************
 * Copyright (c) 2011, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.ICombinedIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ICombinedIntegrationResult;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractCombinedIntegrator implements ICombinedIntegrator {

	public static final String DESCRIPTION = "Combined Integrator";

	protected IProcessingInfo<ICombinedIntegrationResult> validate(IChromatogramSelection<?, ?> chromatogramSelection, ICombinedIntegrationSettings combinedIntegrationSettings) {

		IProcessingInfo<ICombinedIntegrationResult> processingInfo = new ProcessingInfo<>();
		if(chromatogramSelection == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The given chromatogram selection must not be null.");
		}
		if(combinedIntegrationSettings == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The given integration settings must not be null");
		}
		return processingInfo;
	}
}
