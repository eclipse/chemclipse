/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMeasurement extends AbstractMeasurementInfo implements IMeasurement, IMeasurementInfo {

	private static final long serialVersionUID = 3213312019738373785L;
	//
	private Map<String, IMeasurementResult> measurementResults = new HashMap<String, IMeasurementResult>();

	@Override
	public void addMeasurementResult(IMeasurementResult chromatogramResult) {

		if(chromatogramResult != null) {
			measurementResults.put(chromatogramResult.getIdentifier(), chromatogramResult);
		}
	}

	@Override
	public IMeasurementResult getMeasurementResult(String identifier) {

		return measurementResults.get(identifier);
	}

	@Override
	public void deleteMeasurementResult(String identifier) {

		measurementResults.remove(identifier);
	}

	@Override
	public void removeAllMeasurementResults() {

		measurementResults.clear();
	}

	@Override
	public Collection<IMeasurementResult> getMeasurementResults() {

		return measurementResults.values();
	}
}
