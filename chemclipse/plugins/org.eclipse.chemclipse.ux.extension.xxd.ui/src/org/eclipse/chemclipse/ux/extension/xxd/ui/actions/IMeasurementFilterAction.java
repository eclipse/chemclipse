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
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsWizard;
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
	private Object settings;

	public IMeasurementFilterAction(IMeasurementFilter<?> filter, Collection<? extends IMeasurement> measurements, Consumer<Collection<? extends IMeasurement>> resultConsumer, ProcessTypeSupport processTypeSupport) {
		super(filter, resultConsumer);
		this.measurements = measurements;
		this.processTypeSupport = processTypeSupport;
		setEnabled(filter.acceptsIMeasurements(measurements));
	}

	@Override
	public void executeAction(Shell shell) {

		if(processTypeSupport != null) {
			IProcessSupplier<?> processSupplier = processTypeSupport.getSupplier(filter.getID());
			if(processSupplier != null) {
				try {
					settings = SettingsWizard.getSettings(shell, ProcessTypeSupport.getWorkspacePreferences(processSupplier));
				} catch(IOException e) {
					ProcessingInfoViewSupport.updateProcessingInfoError(filter.getName(), "Can't process settings", e);
				} catch(CancellationException e) {
					return;
				}
			}
		}
		super.executeAction(shell);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	protected Collection<? extends IMeasurement> computeResult(MessageConsumer messageConsumer, IProgressMonitor progressMonitor) {

		return (Collection<? extends IMeasurement>)((IMeasurementFilter)filter).filterIMeasurements(measurements, settings, Function.identity(), messageConsumer, progressMonitor);
	}
}
