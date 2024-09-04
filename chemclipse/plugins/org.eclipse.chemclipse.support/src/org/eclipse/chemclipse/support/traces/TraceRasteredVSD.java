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
 * FT-IR
 * NIR
 * Raman
 * ...
 */
public class TraceRasteredVSD extends AbstractTrace {

	public int getWavenumber() {

		return getValueAsInt();
	}

	public void setWavenumber(double wavenumber) {

		setValue(wavenumber);
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getWavenumber());
		builder.append(getScaleFactorAsString());
		//
		return builder.toString();
	}
}