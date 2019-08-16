/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.settings;

import org.eclipse.chemclipse.model.identifier.AbstractIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AbstractIdentifierSettingsMSD extends AbstractIdentifierSettings implements IIdentifierSettingsMSD {

	@JsonIgnore
	private String massSpectrumComparatorId = "";
	@JsonIgnore
	private IMarkedIons excludedIons;

	public AbstractIdentifierSettingsMSD() {
		excludedIons = new MarkedIons();
	}

	@Override
	public String getMassSpectrumComparatorId() {

		return massSpectrumComparatorId;
	}

	@Override
	public void setMassSpectrumComparatorId(String massSpectrumComparatorId) {

		this.massSpectrumComparatorId = massSpectrumComparatorId;
	}

	@Override
	public IMarkedIons getExcludedIons() {

		return excludedIons;
	}
}
