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
package org.eclipse.chemclipse.xxd.process.supplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.filter.IMeasurementFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.processing.ProcessorFactory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.IMeasurementProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class IMeasurementFilterProcessTypeSupplier extends AbstractFilterFactoryProcessTypeSupplier<IMeasurement, IMeasurementFilter<?>> implements IChromatogramSelectionProcessTypeSupplier, IMeasurementProcessTypeSupplier {

	public IMeasurementFilterProcessTypeSupplier(ProcessorFactory filterFactory) {
		super(filterFactory);
		Collection<IMeasurementFilter<?>> filters = filterFactory.getProcessors(ProcessorFactory.genericClass(IMeasurementFilter.class), null);
		for(IMeasurementFilter<?> filter : filters) {
			createProcessorSupplier(filter);
		}
	}

	@Override
	public String getCategory() {

		return "Filter";
	}

	@Override
	public IProcessingInfo<IChromatogramSelection<?, ?>> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		FilterProcessSupplier<IMeasurementFilter<?>> supplier = getProcessorSupplier(processorId);
		if(supplier != null) {
			ProcessingInfo<IChromatogramSelection<?, ?>> info = new ProcessingInfo<>();
			info.setProcessingResult(chromatogramSelection);
			IMeasurementFilter<?> filter = supplier.getFilter();
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			Set<IChromatogram<?>> chromatograms = Collections.singleton(chromatogram);
			if(filter.acceptsIMeasurements(chromatograms)) {
				// TODO currently the caller always assume that the chromatogram is modified directly, we might want to change this in the future but keep it here for backward compat in fact there is also no way to set the selected chromatogram anyways
				apply(chromatograms, processSettings, info, monitor, filter);
			} else {
				info.addErrorMessage(filter.getName(), "This Filter can't handle a Chromatogram of type " + chromatogram.getClass().getSimpleName());
			}
			return info;
		}
		return null;
	}

	@Override
	public Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, String processorId, Object processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor) {

		FilterProcessSupplier<IMeasurementFilter<?>> supplier = getProcessorSupplier(processorId);
		if(supplier != null) {
			IMeasurementFilter<?> filter = supplier.getFilter();
			if(filter.acceptsIMeasurements(measurements)) {
				// we can process them as a whole...
				return apply(measurements, processSettings, messageConsumer, monitor, filter);
			} else {
				// check if we at least can process single items of the list...
				List<IMeasurement> resultList = new ArrayList<>();
				SubMonitor subMonitor = SubMonitor.convert(monitor, measurements.size());
				for(IMeasurement measurement : measurements) {
					Set<IMeasurement> singleItem = Collections.singleton(measurement);
					if(filter.acceptsIMeasurements(singleItem)) {
						// we can process this single item ...
						resultList.addAll(apply(singleItem, processSettings, messageConsumer, subMonitor.split(1), filter));
					} else {
						// we can't process this item either...
						resultList.add(measurement);
						subMonitor.worked(1);
					}
				}
				return resultList;
			}
		}
		return measurements;
	}

	private <T> Collection<? extends IMeasurement> apply(Collection<? extends IMeasurement> measurements, Object processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor, IMeasurementFilter<T> filter) {

		T configuration;
		try {
			configuration = filter.getConfigClass().cast(processSettings);
		} catch(ClassCastException e) {
			messageConsumer.addWarnMessage(filter.getName(), "Invalid Configuration, using default instead");
			configuration = filter.createConfiguration(measurements);
		}
		return filter.filterIMeasurements(measurements, configuration, Function.identity(), messageConsumer, monitor);
	}
}