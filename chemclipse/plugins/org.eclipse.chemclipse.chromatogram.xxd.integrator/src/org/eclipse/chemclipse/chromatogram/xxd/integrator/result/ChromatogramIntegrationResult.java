/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.result;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;

public class ChromatogramIntegrationResult extends AbstractChromatogramIntegrationResult implements IChromatogramIntegrationResult {

	public ChromatogramIntegrationResult(double ion, double chromatogramArea, double backgroundArea) {
		super(ion, chromatogramArea, backgroundArea);
	}

	/**
	 * Set the TIC result.
	 * 
	 * @param chromatogramArea
	 * @param backgroundArea
	 */
	public ChromatogramIntegrationResult(double chromatogramArea, double backgroundArea) {
		super(AbstractIon.TIC_ION, chromatogramArea, backgroundArea);
	}
}
