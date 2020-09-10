/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.identifier.IdentifierAdapterSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Default settings class, which sets all identifier settings values
 * to their defaults. Additionally, no JsonAnnotations are declared, so
 * that each identifier settings class, which don't need the underlying
 * settings, can re-use this default class.
 */
public abstract class IdentifierAdapterSettingsMSD extends IdentifierAdapterSettings implements IIdentifierSettingsMSD {

	@JsonIgnore
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
