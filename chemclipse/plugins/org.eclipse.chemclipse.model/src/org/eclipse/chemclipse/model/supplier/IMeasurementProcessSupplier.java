/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.supplier;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;

public interface IMeasurementProcessSupplier<ConfigType> extends IProcessSupplier<ConfigType> {

	/**
	 * Apply the given processor ID to the given list of measurements
	 * 
	 * @param measurements
	 *            the measurements to process
	 * @param processorId
	 *            the id of the processor to apply
	 * @param processSettings
	 *            the settings to use or <code>null</code> if default settings are in effect
	 * @param messageConsumer
	 *            the consumer to listen for feedback messages
	 * @param monitor
	 *            the monitor to use to report progress
	 * @return the collection of processed measurements
	 */
	Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, ConfigType processSettings, ProcessExecutionContext context);

	static <X> Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, IProcessSupplier<X> supplier, X processSettings, ProcessExecutionContext context) {

		if(supplier instanceof IMeasurementProcessSupplier<?>) {
			IMeasurementProcessSupplier<X> measurementProcessSupplier = (IMeasurementProcessSupplier<X>)supplier;
			return measurementProcessSupplier.applyProcessor(measurements, processSettings, context);
		} else if(supplier instanceof ProcessEntryContainer) {
			ProcessEntryContainer container = (ProcessEntryContainer)supplier;
			return applyProcessEntries(measurements, container, context);
		}
		return measurements;
	}

	static <X> Collection<? extends IMeasurement> applyProcessEntries(Collection<? extends IMeasurement> measurements, ProcessEntryContainer processMethod, ProcessExecutionContext context) {

		AtomicReference<Collection<? extends IMeasurement>> result = new AtomicReference<Collection<? extends IMeasurement>>(measurements);
		ProcessEntryContainer.applyProcessEntries(processMethod, context, (preferences, subContext) -> {
			try {
				result.set(applyProcessor(result.get(), preferences.getSupplier(), preferences.getSettings(), subContext));
			} catch(IOException e) {
				subContext.addWarnMessage(preferences.getSupplier().getName(), "reading settings failed, processor will be skipped", e);
			}
		});
		return result.get();
	}
}
