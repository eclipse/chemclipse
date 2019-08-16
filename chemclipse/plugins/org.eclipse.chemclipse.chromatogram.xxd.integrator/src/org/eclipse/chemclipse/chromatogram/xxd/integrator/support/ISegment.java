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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.support;

import org.eclipse.chemclipse.numeric.core.IPoint;

public interface ISegment {

	IPoint getPeakBaselinePoint1();

	void setPeakBaselinePoint1(IPoint point);

	IPoint getPeakBaselinePoint2();

	void setPeakBaselinePoint2(IPoint point);

	IPoint getChromatogramBaselinePoint1();

	void setChromatogramBaselinePoint1(IPoint point);

	IPoint getChromatogramBaselinePoint2();

	void setChromatogramBaselinePoint2(IPoint point);
	/*
	 * The area methods are implemented in SegmentAreaCalculator.
	 */
}
