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
package org.eclipse.chemclipse.xxd.process.support;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IMeasurementProcessTypeSupplier extends IProcessTypeSupplier {

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
	Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, String processorId, Object processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor);
}
