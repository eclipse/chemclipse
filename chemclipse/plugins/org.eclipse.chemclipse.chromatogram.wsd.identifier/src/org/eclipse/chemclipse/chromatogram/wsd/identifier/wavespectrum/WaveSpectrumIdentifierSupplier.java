/*******************************************************************************
 * Copyright (c) 2010, 2021 Lablicate GmbH.
 *
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Jan Holy - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.identifier.wavespectrum;

import org.eclipse.chemclipse.chromatogram.wsd.identifier.settings.IWaveSpectrumIdentifierSettings;
import org.eclipse.chemclipse.model.identifier.core.AbstractSupplier;

public class WaveSpectrumIdentifierSupplier extends AbstractSupplier<IWaveSpectrumIdentifierSettings> implements IWaveSpectrumIdentifierSupplier {

	@Override
	public Class<? extends IWaveSpectrumIdentifierSettings> getSettingsClass() {

		return getSpecificIdentifierSettingsClass();
	}
}
