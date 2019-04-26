/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.core.AbstractMeasurement;

public class SimpleFIDMeasurement extends AbstractMeasurement
implements FIDMeasurementBody<SimpleFIDSignal>, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2882781702234223800L;
	private DataDimension dimension;
	private List<SimpleFIDSignal> signals;

	public SimpleFIDMeasurement(FIDMeasurement template) {
		this.dimension = template.getDataDimension();
		this.signals = SimpleFIDSignal.copy(template.getSignals());
	}

	public SimpleFIDMeasurement() {
		signals = new ArrayList<>();
	}

	@Override
	public Collection<SimpleFIDSignal> getSignals() {
		return Collections.unmodifiableCollection(signals);
	}

	@Override
	public DataDimension getDataDimension() {
		return dimension;
	}

}
