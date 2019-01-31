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

public interface IScanNMRInfo {

	Optional<Double> getIrradiationCarrierFrequency();

	void setIrradiationCarrierFrequency(double frequency);

	double getSweepWidth();

	double getFirstDataPointOffset();

	Optional<ISpectralReference> getSpectralRefence();

	void setSpectralReference(ISpectralReference spectral_reference);
}
