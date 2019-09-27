/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.supplier.IMeasurementProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.xxd.process.Activator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class ProcessTypeSupport implements ProcessSupplierContext {

	private List<IProcessTypeSupplier> localProcessSupplier = new ArrayList<>();

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
		IProcessTypeSupplier[] dynamic = Activator.geIProcessTypeSuppliers();
		for(IProcessTypeSupplier typeSupplier : dynamic) {
			for(IProcessSupplier<?> supplier : typeSupplier.getProcessorSuppliers()) {
				if(supplier.matchesId(processorId)) {
					return (IProcessSupplier<SettingType>)supplier;
				}
			}
		}
		return null;
	}

	/**
	 * Get all suppliers matching a given set of datacategories
	 * 
	 * @param dataTypes
	 * @return the matching {@link IProcessSupplier}
	 */
	public Set<IProcessSupplier<?>> getSupplier(Iterable<DataCategory> dataTypes) {

		Set<IProcessSupplier<?>> supplier = new TreeSet<>((o1, o2) -> o1.getId().compareTo(o2.getId()));
		addMatchingSupplier(dataTypes, supplier, localProcessSupplier.toArray(new IProcessTypeSupplier[0]));
		addMatchingSupplier(dataTypes, supplier, Activator.geIProcessTypeSuppliers());
		return supplier;
	}

	private void addMatchingSupplier(Iterable<DataCategory> dataTypes, Set<IProcessSupplier<?>> supplier, IProcessTypeSupplier[] processTypeSuppliers) {

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
	 * {@link ProcessTypeSupport} this allows for adding non standard supplier
	 * 
	 * @param processTypeSupplier
	 */
	public void addProcessSupplier(IProcessTypeSupplier processTypeSupplier) {

		localProcessSupplier.add(processTypeSupplier);
	}

	@Deprecated
	public <T> IProcessingInfo<T> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, IProcessMethod processMethod, IProgressMonitor monitor) {

		ProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		IChromatogramSelectionProcessSupplier.applyProcessMethod(chromatogramSelection, processMethod, this, processingInfo, monitor);
		return processingInfo;
	}

	@Deprecated
	public <T, X> IProcessingInfo<T> applyProcessor(List<? extends IChromatogramSelection<?, ?>> chromatogramSelections, IProcessMethod processMethod, IProgressMonitor monitor) {

		ProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		SubMonitor subMonitor = SubMonitor.convert(monitor, chromatogramSelections.size() * 100);
		for(IChromatogramSelection<?, ?> selection : chromatogramSelections) {
			IChromatogramSelectionProcessSupplier.applyProcessMethod(selection, processMethod, this, processingInfo, subMonitor.split(100));
		}
		return processingInfo;
	}

	@Deprecated
	public <X> Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, IProcessMethod processMethod, MessageConsumer messageConsumer, IProgressMonitor monitor) {

		return IMeasurementProcessSupplier.applyProcessMethod(measurements, processMethod, this, messageConsumer, monitor);
	}
}
