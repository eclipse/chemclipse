/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - improve serializable support
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractMeasurement extends AbstractMeasurementInfo implements IMeasurement, IMeasurementInfo {

	private static final long serialVersionUID = 3213312019738373785L;
	//
	private transient Map<String, IMeasurementResult<?>> measurementResultsMap;

	public AbstractMeasurement() {

		super();
	}

	public AbstractMeasurement(Map<String, String> headerData) {

		super();
		putHeaderData(headerData);
	}

	@Override
	public void addMeasurementResult(IMeasurementResult<?> chromatogramResult) {

		if(chromatogramResult != null) {
			getMeasurementResultsMap().put(chromatogramResult.getIdentifier(), chromatogramResult);
		}
	}

	@Override
	public IMeasurementResult<?> getMeasurementResult(String identifier) {

		return getMeasurementResultsMap().get(identifier);
	}

	@Override
	public void deleteMeasurementResult(String identifier) {

		getMeasurementResultsMap().remove(identifier);
	}

	@Override
	public void removeAllMeasurementResults() {

		getMeasurementResultsMap().clear();
	}

	@Override
	public Collection<IMeasurementResult<?>> getMeasurementResults() {

		return getMeasurementResultsMap().values();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

		out.defaultWriteObject();
		List<IMeasurementResult<?>> serializableResults = new ArrayList<IMeasurementResult<?>>();
		for(IMeasurementResult<?> result : getMeasurementResultsMap().values()) {
			if(result instanceof Serializable) {
				serializableResults.add(result);
			}
		}
		out.writeObject(serializableResults);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

		in.defaultReadObject();
		@SuppressWarnings("unchecked")
		List<IMeasurementResult<?>> results = (List<IMeasurementResult<?>>)in.readObject();
		for(IMeasurementResult<?> result : results) {
			// we don't call the setter here (as this might be overriden) to make sure we only put our private items back
			getMeasurementResultsMap().put(result.getIdentifier(), result);
		}
	}

	public Map<String, IMeasurementResult<?>> getMeasurementResultsMap() {

		if(measurementResultsMap == null) {
			measurementResultsMap = new LinkedHashMap<>();
		}
		return measurementResultsMap;
	}
}
