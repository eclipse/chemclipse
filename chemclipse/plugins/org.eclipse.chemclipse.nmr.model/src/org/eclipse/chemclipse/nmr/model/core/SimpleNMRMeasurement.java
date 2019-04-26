/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 * Alexander Kerner - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SimpleNMRMeasurement implements SpectrumMeasurementBody, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1821879076290727455L;

	private Collection<SpectrumSignal> signals = new ArrayList<>();

	@Override
	public Collection<SpectrumSignal> getSignals() {
		return Collections.unmodifiableCollection(signals);
	}

	public void setSignals(Collection<? extends SpectrumSignal> signals) {
		this.signals = new ArrayList<>(signals);
	}



}
