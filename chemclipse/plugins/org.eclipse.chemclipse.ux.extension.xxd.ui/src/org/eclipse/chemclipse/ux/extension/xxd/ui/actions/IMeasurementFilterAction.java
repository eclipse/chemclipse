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

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.filter.IMeasurementFilter;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ProcessorSupplierMenuEntry;
import org.eclipse.chemclipse.xxd.process.support.IProcessSupplier;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

/**
 * An action that support the filtering {@link IMeasurement}s with a {@link IMeasurementFilter}. It will show a progressbar while filtering is in progress
 * 
 * @author Christoph Läubrich
 *
 */
public class IMeasurementFilterAction extends AbstractFilterAction<IMeasurementFilter<?>, Collection<? extends IMeasurement>> {

	private Collection<? extends IMeasurement> measurements;
	private ProcessTypeSupport processTypeSupport;

	public IMeasurementFilterAction(IMeasurementFilter<?> filter, Collection<? extends IMeasurement> measurements, Consumer<Collection<? extends IMeasurement>> resultConsumer, ProcessTypeSupport processTypeSupport) {
		super(filter, resultConsumer);
		this.measurements = measurements;
		this.processTypeSupport = processTypeSupport;
		setEnabled(filter.acceptsIMeasurements(measurements));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	protected Collection<? extends IMeasurement> computeResult(Shell shell, MessageConsumer messageConsumer, IProgressMonitor progressMonitor) {

		if(processTypeSupport != null) {
			IProcessTypeSupplier supplier = processTypeSupport.getSupplier(filter.getID());
			IProcessSupplier processSupplier = supplier.getProcessorSupplier(filter.getID());
			try {
				Object settings = ProcessorSupplierMenuEntry.getSettings(shell, processSupplier);
				((IMeasurementFilter)filter).filterIMeasurements(measurements, settings, Function.identity(), messageConsumer, progressMonitor);
			} catch(IOException e) {
				// logger.warn(e);
			} catch(CancellationException e) {
				//
			}
			System.out.println();
		}
		return filter.createIMeasurementFilterFunction(progressMonitor, messageConsumer, Function.identity()).apply(measurements);
	}
}
