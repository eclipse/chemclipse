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
package org.eclipse.chemclipse.ux.extension.xxd.ui.actions;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.filter.IMeasurementFilter;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * An action that support the filtering {@link IMeasurement}s with a {@link IMeasurementFilter}. It will show a progressbar while filtering is in progress
 * 
 * @author Christoph Läubrich
 *
 */
public class IMeasurementFilterAction extends AbstractFilterAction<IMeasurementFilter<?>, Collection<? extends IMeasurement>> {

	private Collection<? extends IMeasurement> measurements;

	public IMeasurementFilterAction(IMeasurementFilter<?> filter, Collection<? extends IMeasurement> measurements, Consumer<Collection<? extends IMeasurement>> resultConsumer) {
		super(filter, resultConsumer);
		this.measurements = measurements;
		setEnabled(filter.acceptsIMeasurements(measurements));
	}

	@Override
	protected Collection<? extends IMeasurement> computeResult(MessageConsumer messageConsumer, IProgressMonitor progressMonitor) {

		return filter.createIMeasurementFilterFunction(progressMonitor, messageConsumer, Function.identity()).apply(measurements);
	}
}
