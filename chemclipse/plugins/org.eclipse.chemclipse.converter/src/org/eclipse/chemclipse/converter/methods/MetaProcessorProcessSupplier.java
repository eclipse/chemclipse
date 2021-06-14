/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - header was missing
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.util.function.BiFunction;

import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutor;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;

public final class MetaProcessorProcessSupplier extends AbstractProcessSupplier<MetaProcessorSettings> implements ProcessExecutor {

	private final IProcessMethod processMethod;

	public MetaProcessorProcessSupplier(String id, IProcessMethod processMethod, MethodProcessTypeSupplier parent) {

		super(id, processMethod.getName(), processMethod.getDescription(), processMethod.isFinal() ? null : MetaProcessorSettings.class, parent, MethodProcessSupport.getDataTypes(processMethod));
		this.processMethod = processMethod;
	}

	@Override
	public String getCategory() {

		return processMethod.getCategory();
	}

	public IProcessMethod getProcessMethod() {

		return processMethod;
	}

	@Override
	public <X> void execute(ProcessorPreferences<X> preferences, ProcessExecutionContext context) throws Exception {

		X settings = preferences.getSettings();
		if(settings instanceof MetaProcessorSettings) {
			MetaProcessorSettings processorSettings = (MetaProcessorSettings)settings;
			ProcessExecutionConsumer<?> callerDelegate = context.getContextObject(ProcessExecutionConsumer.class);
			if(callerDelegate != null) {
				ProcessEntryContainer.applyProcessEntries(processMethod, context, new BiFunction<IProcessEntry, IProcessSupplier<X>, ProcessorPreferences<X>>() {

					@Override
					public ProcessorPreferences<X> apply(IProcessEntry processEntry, IProcessSupplier<X> processSupplier) {

						return processorSettings.getProcessorPreferences(processEntry, processEntry.getPreferences(processSupplier));
					}
				}, callerDelegate);
			}
		}
	}
}