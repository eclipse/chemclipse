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

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;

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

	static ProcessExecutionConsumer<Collection<? extends IMeasurement>> createConsumer(Collection<? extends IMeasurement> measurements) {

		return new ProcessExecutionConsumer<Collection<? extends IMeasurement>>() {

			AtomicReference<Collection<? extends IMeasurement>> result = new AtomicReference<Collection<? extends IMeasurement>>(measurements);

			@Override
			public <X> void execute(ProcessorPreferences<X> preferences, ProcessExecutionContext context) throws Exception {

				IProcessSupplier<X> supplier = preferences.getSupplier();
				if(supplier instanceof IMeasurementProcessSupplier<?>) {
					IMeasurementProcessSupplier<X> measurementProcessSupplier = (IMeasurementProcessSupplier<X>)supplier;
					result.set(measurementProcessSupplier.applyProcessor(result.get(), preferences.getSettings(), context));
				}
			}

			@Override
			public Collection<? extends IMeasurement> getResult() {

				return result.get();
			}

			@Override
			public <X> boolean canExecute(ProcessorPreferences<X> preferences) {

				IProcessSupplier<X> supplier = preferences.getSupplier();
				return supplier instanceof IMeasurementProcessSupplier<?>;
			}
		};
	}
}
