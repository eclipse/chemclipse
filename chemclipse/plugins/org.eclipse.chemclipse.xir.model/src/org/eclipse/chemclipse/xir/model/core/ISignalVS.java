/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.core;

import java.io.Serializable;

import org.eclipse.chemclipse.model.core.ISignal;

// Vibration Spectroscopy Signal
public interface ISignalVS extends ISignal, Serializable {

	double getWavenumber();

	void setWavenumber(double wavenumber);

	double getIntensity();

	void setIntensity(double intensity);
}