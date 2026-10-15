/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.calculator.noise.loess;

import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.AbstractMassSpectrumFilterSettings;
import org.eclipse.chemclipse.msd.model.core.MassSpectrumType;
import org.eclipse.chemclipse.processing.core.ICategories;

public class LoessInterpolatorFilterSettings extends AbstractMassSpectrumFilterSettings {

	@Override
	public List<MassSpectrumType> appliesToMassSpectrumTypes() {

		return Arrays.asList(MassSpectrumType.PROFILE);
	}

	@Override
	public String getCategory() {

		return ICategories.NOISE_ESTIMATOR;
	}
}
