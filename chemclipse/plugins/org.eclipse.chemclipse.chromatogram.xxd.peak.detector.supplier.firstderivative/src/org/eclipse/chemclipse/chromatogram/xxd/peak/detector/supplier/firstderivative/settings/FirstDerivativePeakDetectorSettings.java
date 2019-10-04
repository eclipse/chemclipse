/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - derived from WSD/MSD/CSD variants
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings;

import org.eclipse.chemclipse.model.types.DataType;

public class FirstDerivativePeakDetectorSettings extends PeakDetectorSettingsMSD {

	public FirstDerivativePeakDetectorSettings() {

		this(DataType.MSD);
	}

	public FirstDerivativePeakDetectorSettings(DataType dataType) {

		// we could load optimized settings depending on datatype here
	}
}
