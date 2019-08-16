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
package org.eclipse.chemclipse.chromatogram.peak.detector.support;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.geometry.Slope;

/**
 * @author eselmeister
 */
public class DetectorSlope extends Slope implements IDetectorSlope {

	private int retentionTime;

	public DetectorSlope(IPoint p1, IPoint p2, int retentionTime) {
		super(p1, p2);
		this.retentionTime = retentionTime;
	}

	@Override
	public int getRetentionTime() {

		return retentionTime;
	}

	@Override
	public String getDrift() {

		String drift = "0";
		/*
		 * Strings are retrieved from the string pool. So there is no overhead.
		 */
		if(getSlope() > 0) {
			drift = "+";
		} else if(getSlope() < 0) {
			drift = "-";
		}
		return drift;
	}

	// --------------equals, hashCode, toString
	@Override
	public boolean equals(Object other) {

		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(getClass() != other.getClass()) {
			return false;
		}
		DetectorSlope otherSlope = (DetectorSlope)other;
		return getRetentionTime() == otherSlope.getRetentionTime();
	}

	@Override
	public int hashCode() {

		return 7 * Integer.valueOf(retentionTime).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("retentionTime=" + retentionTime);
		builder.append("]");
		return builder.toString();
	}
	// --------------equals, hashCode, toString
}
