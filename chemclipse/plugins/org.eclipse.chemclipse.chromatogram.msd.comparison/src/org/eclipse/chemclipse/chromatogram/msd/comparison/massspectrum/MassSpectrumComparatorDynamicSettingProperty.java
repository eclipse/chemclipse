/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Christoph Läubrich - complete refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import java.util.Collection;

import org.eclipse.chemclipse.chromatogram.msd.comparison.exceptions.NoMassSpectrumComparatorAvailableException;
import org.eclipse.chemclipse.support.settings.ComboSettingsProperty.ComboSupplier;

public class MassSpectrumComparatorDynamicSettingProperty implements ComboSupplier<IMassSpectrumComparisonSupplier> {

	@Override
	public Collection<IMassSpectrumComparisonSupplier> items() {

		return MassSpectrumComparator.getMassSpectrumComparatorSupport().getSuppliers();
	}

	@Override
	public IMassSpectrumComparisonSupplier fromString(String string) {

		try {
			return MassSpectrumComparator.getMassSpectrumComparatorSupport().getMassSpectrumComparisonSupplier(string);
		} catch(NoMassSpectrumComparatorAvailableException e) {
			return null;
		}
	}

	@Override
	public String asString(IMassSpectrumComparisonSupplier item) {

		return item.getId();
	}
}
