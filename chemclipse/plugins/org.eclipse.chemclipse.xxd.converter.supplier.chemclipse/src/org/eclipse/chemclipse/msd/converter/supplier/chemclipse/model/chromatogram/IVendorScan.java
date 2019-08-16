/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram;

import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;

public interface IVendorScan extends IVendorMassSpectrum {

	/**
	 * MAX_IONS The total amount of ions to be stored in the
	 * chemclipse chromatogram.<br/>
	 * It does not mean, that ion 65535 is the upper bound, but only 65535 mass
	 * fragments can be stored in a mass spectrum.
	 */
	int MAX_IONS = 65535;
	int MIN_RETENTION_TIME = 0;
	int MAX_RETENTION_TIME = Integer.MAX_VALUE;
}
