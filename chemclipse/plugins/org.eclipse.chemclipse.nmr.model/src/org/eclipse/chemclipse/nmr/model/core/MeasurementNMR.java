/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Christoph Läubrich - cleanup API
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.model.core.AbstractMeasurement;

public class MeasurementNMR extends AbstractMeasurement implements IMeasurementNMR {

	/**
	 *
	 */
	private static final long serialVersionUID = -3880531822942318441L;
	//
	private IScanNMR scanNMR;
	private IScanFID scanFID;
	private Map<String, Double> parameters = new HashMap<>();

	public MeasurementNMR() {
		scanNMR = new ScanNMR();
		scanFID = new ScanFID();
	}

	@Override
	public IScanNMR getScanMNR() {

		return scanNMR;
	}

	public void setScanMMR(IScanNMR scanNMR) {

		this.scanNMR = scanNMR;
	}

	@Override
	public IScanFID getScanFID() {

		return scanFID;
	}

	public void setScanFID(IScanFID scanFID) {

		this.scanFID = scanFID;
	}

	public void putProcessingParameters(String name, Double parameter) {

		parameters.put(name, parameter);
	}

	@Override
	public Double getProcessingParameters(String name) {

		return parameters.get(name);
	}
}
