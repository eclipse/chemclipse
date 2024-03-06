/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.vsd.model.implementation;

import org.eclipse.chemclipse.vsd.model.core.AbstractPeakVSD;
import org.eclipse.chemclipse.vsd.model.core.IPeakModelVSD;
import org.eclipse.chemclipse.vsd.model.core.IPeakVSD;

public class PeakVSD extends AbstractPeakVSD implements IPeakVSD {

	public PeakVSD(IPeakModelVSD peakModel, String modelDescription) throws IllegalArgumentException {

		super(peakModel, modelDescription);
	}

	public PeakVSD(IPeakModelVSD peakModel) throws IllegalArgumentException {

		super(peakModel);
	}

	public PeakVSD(IPeakVSD template) {

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