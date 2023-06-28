/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.core.IntegrationType;

public class IntegrationEntry implements IIntegrationEntry {

	private static final long serialVersionUID = -4615194962387454532L;
	//
	private final double signal;
	private final double integratedArea;
	private IntegrationType integrationType = IntegrationType.NONE;

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

	@Override
	public IntegrationType getIntegrationType() {

		return integrationType;
	}

	@Override
	public void setIntegrationType(IntegrationType integrationType) {

		this.integrationType = integrationType;
	}
}