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
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.ISignal;

public class IntegrationEntry implements IIntegrationEntry {

	private static final long serialVersionUID = 1948207146553081259L;
	//
	private final double signal;
	private final double integratedArea;

	public IntegrationEntry(double integratedArea) {
		this(ISignal.TOTAL_INTENSITY, integratedArea);
	}

	public IntegrationEntry(double signal, double integratedArea) {
		this.signal = signal;
		this.integratedArea = integratedArea;
	}

	@Override
	public double getSignal() {

		return signal;
	}

	@Override
	public double getIntegratedArea() {

		return integratedArea;
	}
}
