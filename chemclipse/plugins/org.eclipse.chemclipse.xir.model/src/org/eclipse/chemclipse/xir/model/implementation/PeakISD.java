/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.implementation;

import org.eclipse.chemclipse.xir.model.core.AbstractPeakISD;
import org.eclipse.chemclipse.xir.model.core.IPeakISD;
import org.eclipse.chemclipse.xir.model.core.IPeakModelISD;

public class PeakISD extends AbstractPeakISD implements IPeakISD {

	public PeakISD(IPeakModelISD peakModel, String modelDescription) throws IllegalArgumentException {

		super(peakModel, modelDescription);
	}

	public PeakISD(IPeakModelISD peakModel) throws IllegalArgumentException {

		super(peakModel);
	}

	public PeakISD(IPeakISD template) {

		this(template.getPeakModel());
		setActiveForAnalysis(template.isActiveForAnalysis());
		setDetectorDescription(template.getDetectorDescription());
		setIntegratedArea(template.getIntegrationEntries(), template.getIntegratorDescription());
		setIntegratorDescription(template.getIntegratorDescription());
		setModelDescription(template.getModelDescription());
		setPeakType(template.getPeakType());
		setQuantifierDescription(template.getQuantifierDescription());
		setSuggestedNumberOfComponents(template.getSuggestedNumberOfComponents());
		getTargets().addAll(template.getTargets());
	}
}