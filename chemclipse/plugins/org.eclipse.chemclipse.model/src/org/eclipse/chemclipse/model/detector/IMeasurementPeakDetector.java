/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.detector;

import java.util.Collection;
import java.util.Map;

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.core.PeakList;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.detector.Detector;
import org.eclipse.chemclipse.processing.detector.DetectorCategory;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IMeasurementPeakDetector<ConfigType> extends Detector<ConfigType> {

	/**
	 * Detects the peaks in the given measurements and returns a mapping between the measurement and the detected peaks
	 * 
	 * @param detectorInputItems
	 * @param configuration
	 * @param messageConsumer
	 * @param monitor
	 * @return
	 * @throws IllegalArgumentException
	 */
	<T extends IMeasurement> Map<T, PeakList> detectIMeasurementPeaks(Collection<T> detectorInputItems, ConfigType configuration, IMessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException;

	/**
	 * Checks if the given {@link IMeasurement} is compatible with this filter, that means that this filter can be applied without throwing an {@link IllegalArgumentException}
	 * 
	 * @param item
	 *            the {@link IMeasurement} to check
	 * @return <code>true</code> if this {@link IMeasurement} can be applied, <code>false</code> otherwise
	 */
	boolean acceptsIMeasurements(Collection<? extends IMeasurement> items);

	/**
	 * Creates a new configuration that is specially suited for the given {@link IMeasurement} types
	 * 
	 * @param item
	 * @return a new configuration for this items or the default config if items is empty or no suitable configuration can be created
	 * @throws IllegalArgumentException
	 *             if the given {@link IMeasurement}s are incompatible with this filter ({@link #acceptsIMeasurements(IMeasurement)} returns <code>false</code>)
	 */
	default ConfigType createConfiguration(Collection<? extends IMeasurement> items) throws IllegalArgumentException {

		if(acceptsIMeasurements(items)) {
			return createNewConfiguration();
		} else {
			throw new IllegalArgumentException("incompatible items in collection");
		}
	}

	@Override
	default DetectorCategory getDetectorCategory() {

		return DetectorCategory.PEAK;
	}
}
