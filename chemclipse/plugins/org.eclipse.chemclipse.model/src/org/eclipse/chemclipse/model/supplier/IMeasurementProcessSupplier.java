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
import java.util.function.BiConsumer;

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

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
	Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, ConfigType processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor);

	static <X> Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, IProcessSupplier<X> supplier, X processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor) {

		if(supplier instanceof IMeasurementProcessSupplier<?>) {
			IMeasurementProcessSupplier<X> measurementProcessSupplier = (IMeasurementProcessSupplier<X>)supplier;
			return measurementProcessSupplier.applyProcessor(measurements, processSettings, messageConsumer, monitor);
		}
		return measurements;
	}

	static <X> Collection<? extends IMeasurement> applyProcessMethod(Collection<? extends IMeasurement> measurements, IProcessMethod processMethod, ProcessSupplierContext context, MessageConsumer messageConsumer, IProgressMonitor monitor) {

		SubMonitor subMonitor = SubMonitor.convert(monitor, "Processing files", processMethod.getNumberOfEntries() * 100);
		AtomicReference<Collection<? extends IMeasurement>> result = new AtomicReference<Collection<? extends IMeasurement>>(measurements);
		IProcessMethod.applyProcessors(processMethod, context, new BiConsumer<IProcessSupplier<X>, X>() {

			@Override
			public void accept(IProcessSupplier<X> processor, X settings) {

				result.set(applyProcessor(result.get(), processor, settings, messageConsumer, subMonitor.split(100)));
			}
		}, messageConsumer);
		return result.get();
	}
}
