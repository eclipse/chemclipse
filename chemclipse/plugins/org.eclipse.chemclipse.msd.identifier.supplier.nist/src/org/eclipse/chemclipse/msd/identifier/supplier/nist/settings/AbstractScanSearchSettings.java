/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.settings;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.MassSpectrumIdentifierAdapterSettings;

public abstract class AbstractScanSearchSettings extends MassSpectrumIdentifierAdapterSettings implements ISearchSettings {

	@Override
	public int getNumberOfTargets() {

		return 15;
	}

	@Override
	public float getMinMatchFactor() {

		return 70.0f;
	}

	@Override
	public float getMinReverseMatchFactor() {

		return 70.0f;
	}

	@Override
	public int getTimeoutInMinutes() {

		return 20;
	}
}
