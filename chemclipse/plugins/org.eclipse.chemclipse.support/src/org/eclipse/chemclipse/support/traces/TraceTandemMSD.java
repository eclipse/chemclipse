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

import org.eclipse.chemclipse.support.text.ValueFormat;

/**
 * GC-MS/MS
 * LC-MS/MS
 * ...
 */
public class TraceTandemMSD extends AbstractTrace {

	private Double daughterMZ = 0.0d;
	private Double collisionEnergy = 0.0d;

	public int getParentMZ() {

		return getValueAsInt();
	}

	public void setParentMZ(double mz) {

		setValue(mz);
	}

	public double getDaughterMZ() {

		return daughterMZ;
	}

	public void setDaughterMZ(double daughterMZ) {

		this.daughterMZ = daughterMZ;
	}

	public int getCollisionEnergy() {

		return collisionEnergy.intValue();
	}

	public void setCollisionEnergy(double collisionEnergy) {

		this.collisionEnergy = collisionEnergy;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getParentMZ());
		builder.append(" > ");
		builder.append(ValueFormat.getDecimalFormatEnglish("0.0").format(getDaughterMZ()));
		builder.append(" @");
		builder.append(getCollisionEnergy());
		builder.append(getScaleFactorAsString());
		//
		return builder.toString();
	}
}