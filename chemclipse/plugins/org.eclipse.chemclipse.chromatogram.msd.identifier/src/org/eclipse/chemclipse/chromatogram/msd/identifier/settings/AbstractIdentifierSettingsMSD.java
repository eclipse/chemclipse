/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add method to get/set masspectrum comparator
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.settings;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparisonSupplier;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.MassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.MassSpectrumComparatorDynamicSettingProperty;
import org.eclipse.chemclipse.model.identifier.AbstractIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.support.settings.ComboSettingsProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class AbstractIdentifierSettingsMSD extends AbstractIdentifierSettings implements IIdentifierSettingsMSD {

	@JsonProperty(value = "Mass Spectrum Comparator", defaultValue = DEFAULT_COMPARATOR_ID)
	@JsonPropertyDescription(value = "Select the algorithm used for mass spectrum comparison calculation.")
	@ComboSettingsProperty(MassSpectrumComparatorDynamicSettingProperty.class)
	private String massSpectrumComparatorId = DEFAULT_COMPARATOR_ID;
	@JsonIgnore
	private IMarkedIons excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
	@JsonIgnore
	private IMassSpectrumComparator comparator = null; // The comparator will be created dynamically.

	@Override
	public String getMassSpectrumComparatorId() {

		return massSpectrumComparatorId;
	}

	@Override
	public void setMassSpectrumComparatorId(String massSpectrumComparatorId) {

		comparator = null;
		this.massSpectrumComparatorId = massSpectrumComparatorId;
	}

	@Override
	public IMarkedIons getExcludedIons() {

		return excludedIons;
	}

	@JsonIgnore
	public IMassSpectrumComparator getMassSpectrumComparator() {

		if(comparator == null) {
			comparator = MassSpectrumComparator.getMassSpectrumComparator(getMassSpectrumComparatorId());
		}
		return comparator;
	}

	public void setMassSpectrumComparator(IMassSpectrumComparator comparator) {

		IMassSpectrumComparisonSupplier supplier = comparator.getMassSpectrumComparisonSupplier();
		if(supplier != null) {
			this.massSpectrumComparatorId = supplier.getId();
		}
		this.comparator = comparator;
	}
}