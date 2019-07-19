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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.filter.IMeasurementFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.filter.FilterFactory;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class IMeasurementFilterProcessTypeSupplier extends AbstractFilterFactoryProcessTypeSupplier<IMeasurement, IMeasurementFilter<?>> implements IChromatogramSelectionProcessTypeSupplier {

	public IMeasurementFilterProcessTypeSupplier(FilterFactory filterFactory) {
		super(filterFactory);
		Collection<IMeasurementFilter<?>> filters = filterFactory.getFilters(FilterFactory.genericClass(IMeasurementFilter.class), null);
		for(IMeasurementFilter<?> filter : filters) {
			createProcessorSupplier(filter);
		}
	}

	@Override
	public String getCategory() {

		return "Measurement Filter";
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
				// TODO currently the caller always assume that the chromatogram is modified directly, we might want to change this in the future but keep it here for backward compat
				filter.createIMeasurementFilterFunction(monitor, info, Function.identity()).apply(chromatograms);
			} else {
				info.addErrorMessage(filter.getName(), "This Filter can't handle a Chromatogram of type " + chromatogram.getClass().getSimpleName());
			}
			return info;
		}
		return null;
	}
}