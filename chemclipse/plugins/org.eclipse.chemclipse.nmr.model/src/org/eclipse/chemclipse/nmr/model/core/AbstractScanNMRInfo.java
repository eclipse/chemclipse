/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.util.Optional;

public abstract class AbstractScanNMRInfo implements IScanNMRInfo {

	private Optional<Double> irradiationCarrierFrequency = Optional.empty();
	private Optional<ISpectralReference> spectral_reference = Optional.empty();

	@Override
	public Optional<Double> getIrradiationCarrierFrequency() {

		return irradiationCarrierFrequency;
	}

	@Override
	public void setIrradiationCarrierFrequency(double frequency) {

		irradiationCarrierFrequency = Optional.of(frequency);
	}

	@Override
	public Optional<ISpectralReference> getSpectralRefence() {

		return spectral_reference;
	}

	@Override
	public void setSpectralReference(ISpectralReference spectral_reference) {

		this.spectral_reference = Optional.of(spectral_reference);
	}
}
