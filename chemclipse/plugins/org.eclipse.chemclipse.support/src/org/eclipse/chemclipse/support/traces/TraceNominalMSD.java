/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.traces;

/**
 * Quadrupole
 * IonTrap
 * ...
 */
public class TraceNominalMSD extends TraceGeneric {

	public int getMZ() {

		return getValueAsInt();
	}

	public void setMZ(double mz) {

		setValue(mz);
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getMZ());
		builder.append(getScaleFactorAsString());
		//
		return builder.toString();
	}
}