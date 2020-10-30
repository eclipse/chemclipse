/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - improve logging output, propagate processor messages, enhance for usage in editors, preference support for processors
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.supplier.IMeasurementProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.xxd.process.Activator;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @deprecated either use the {@link ProcessSupplierContext} OSGI-Service or the E4ProcessSupplierContext if context injection is desired
 *
 */
@Deprecated
public class ProcessTypeSupport implements ProcessSupplierContext {

	private final List<IProcessTypeSupplier> localProcessSupplier = new ArrayList<>();

	@Override
	@SuppressWarnings("unchecked")
	public <SettingType> IProcessSupplier<SettingType> getSupplier(String processorId) {

		for(IProcessTypeSupplier typeSupplier : localProcessSupplier) {
			for(IProcessSupplier<?> supplier : typeSupplier.getProcessorSuppliers()) {
				if(supplier.matchesId(processorId)) {
					return (IProcessSupplier<SettingType>)supplier;
				}
			}
		}
		//
		IProcessTypeSupplier[] dynamic = Activator.geIProcessTypeSuppliers();
		for(IProcessTypeSupplier typeSupplier : dynamic) {
			for(IProcessSupplier<?> supplier : typeSupplier.getProcessorSuppliers()) {
				if(supplier.matchesId(processorId)) {
					return (IProcessSupplier<SettingType>)supplier;
				}
			}
		}
		//
		return null;
	}

	/**
	 * Get all suppliers matching a given set of datacategories
	 * 
	 * @deprecated use {@link #visitSupplier(Consumer)} instead
	 * @param dataTypes
	 * @return the matching {@link IProcessSupplier}
	 */
	@Deprecated
	public Set<IProcessSupplier<?>> getSupplier(Iterable<DataCategory> dataTypes) {

		Set<IProcessSupplier<?>> supplier = new TreeSet<>((o1, o2) -> o1.getId().compareTo(o2.getId()));
		addMatchingSupplier(dataTypes, supplier, localProcessSupplier.toArray(new IProcessTypeSupplier[0]));
		addMatchingSupplier(dataTypes, supplier, Activator.geIProcessTypeSuppliers());
		return supplier;
	}

	@Override
	public void visitSupplier(Consumer<? super IProcessSupplier<?>> consumer) {

		for(IProcessTypeSupplier typeSupplier : localProcessSupplier) {
			typeSupplier.getProcessorSuppliers().forEach(consumer);
		}
		for(IProcessTypeSupplier typeSupplier : Activator.geIProcessTypeSuppliers()) {
			typeSupplier.getProcessorSuppliers().forEach(consumer);
		}
	}

	private void addMatchingSupplier(Iterable<DataCategory> dataTypes, Set<IProcessSupplier<?>> supplier, IProcessTypeSupplier[] processTypeSuppliers) {

		if(dataTypes == null) {
			for(IProcessTypeSupplier processTypeSupplier : processTypeSuppliers) {
				for(IProcessSupplier<?> processSupplier : processTypeSupplier.getProcessorSuppliers()) {
					supplier.add(processSupplier);
				}
			}
			return;
		}
		for(IProcessTypeSupplier processTypeSupplier : processTypeSuppliers) {
			for(IProcessSupplier<?> processSupplier : processTypeSupplier.getProcessorSuppliers()) {
				for(DataCategory category : dataTypes) {
					if(processSupplier.getSupportedDataTypes().contains(category)) {
						supplier.add(processSupplier);
						break;
					}
				}
			}
		}
	}

	/**
	 * Adds the given {@link IProcessTypeSupplier} to this
	 * 
	 * @param processTypeSupplier
	 */
	public void addProcessSupplier(IProcessTypeSupplier processTypeSupplier) {

		localProcessSupplier.add(processTypeSupplier);
	}

	@Deprecated
	public <T> IProcessingInfo<T> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, IProcessMethod processMethod, IProgressMonitor monitor) {

		ProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		ProcessEntryContainer.applyProcessEntries(processMethod, new ProcessExecutionContext(monitor, processingInfo, this), IChromatogramSelectionProcessSupplier.createConsumer(chromatogramSelection));
		return processingInfo;
	}

	@Deprecated
	public <T, X> IProcessingInfo<T> applyProcessor(List<? extends IChromatogramSelection<?, ?>> chromatogramSelections, IProcessMethod processMethod, IProgressMonitor monitor) {

		ProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		ProcessExecutionContext executionContext = new ProcessExecutionContext(monitor, processingInfo, this);
		executionContext.setWorkRemaining(chromatogramSelections.size());
		for(IChromatogramSelection<?, ?> selection : chromatogramSelections) {
			ProcessEntryContainer.applyProcessEntries(processMethod, executionContext.split(), IChromatogramSelectionProcessSupplier.createConsumer(selection));
		}
		return processingInfo;
	}

	@Deprecated
	public <X> Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, IProcessMethod processMethod, MessageConsumer messageConsumer, IProgressMonitor monitor) {

		return ProcessEntryContainer.applyProcessEntries(processMethod, new ProcessExecutionContext(monitor, messageConsumer, this), IMeasurementProcessSupplier.createConsumer(measurements));
	}
}
