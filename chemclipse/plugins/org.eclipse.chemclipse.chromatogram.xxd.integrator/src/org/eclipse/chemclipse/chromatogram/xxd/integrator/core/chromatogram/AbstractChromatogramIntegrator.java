/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractChromatogramIntegrator<R> implements IChromatogramIntegrator<R> {

	private static final String DESCRIPTON = "Chromatogram Integrator";

	protected IProcessingInfo<R> validate(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramIntegrationSettings chromatogramIntegrationSettings) {

		IProcessingInfo<R> processingInfo = new ProcessingInfo<>();
		if(chromatogramSelection == null) {
			processingInfo.addErrorMessage(DESCRIPTON, "The given chromatogram selection must not be null.");
		}
		if(chromatogramIntegrationSettings == null) {
			processingInfo.addErrorMessage(DESCRIPTON, "The given chromatogram integration settings must not be null");
		}
		return processingInfo;
	}
}
