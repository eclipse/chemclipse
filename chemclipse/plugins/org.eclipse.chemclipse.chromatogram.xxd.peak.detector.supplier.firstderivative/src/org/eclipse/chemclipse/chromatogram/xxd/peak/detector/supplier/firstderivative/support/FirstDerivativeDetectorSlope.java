/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support;

import org.eclipse.chemclipse.chromatogram.peak.detector.support.DetectorSlope;
import org.eclipse.chemclipse.numeric.core.IPoint;

public class FirstDerivativeDetectorSlope extends DetectorSlope implements IFirstDerivativeDetectorSlope {

	public FirstDerivativeDetectorSlope(IPoint p1, IPoint p2, int retentionTime) {

		super(p1, p2, retentionTime);
	}
}
