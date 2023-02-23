/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.implementation;

import org.eclipse.chemclipse.model.core.AbstractMeasurementInfo;
import org.eclipse.chemclipse.xir.model.core.IScanISD;
import org.eclipse.chemclipse.xir.model.core.ISpectrumXIR;

public class SpectrumXIR extends AbstractMeasurementInfo implements ISpectrumXIR {

	private static final long serialVersionUID = 118019458936673272L;
	private IScanISD scanXIR = new ScanISD();

	@Override
	public IScanISD getScanXIR() {

		return scanXIR;
	}

	@Override
	public int hashCode() {

		return scanXIR.hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		return scanXIR.equals(obj);
	}

	@Override
	public String toString() {

		return "SpectrumXIR [rotationAngle=" + scanXIR.getRotationAngle() + "]";
	}
}