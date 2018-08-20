/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import org.eclipse.chemclipse.model.core.AbstractMeasurementInfo;

public class ScanPCR extends AbstractMeasurementInfo implements IScanPCR {

	private static final long serialVersionUID = -5957842720637103236L;

	@Override
	public int hashCode() {

		int result = super.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(!super.equals(obj))
			return false;
		if(getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "ScanPCR";
	}
}
