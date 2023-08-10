/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - improvement update process
 *******************************************************************************/
package org.eclipse.chemclipse.model.supplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.ProcessorFactory;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = {IProcessTypeSupplier.class})
public class PeakFilterProcessTypeSupplier implements IProcessTypeSupplier {

	private ProcessorFactory processorFactory;

	@Override
	public String getCategory() {

		return ICategories.PEAK_FILTER;
	}

	@Reference(unbind = "-")
	public void setProcessorFactory(ProcessorFactory factory) {

		processorFactory = factory;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> list = new ArrayList<>();
		Collection<IPeakFilter<?>> filters = processorFactory.getProcessors(ProcessorFactory.genericClass(IPeakFilter.class), null);
		for(IPeakFilter<?> filter : filters) {
			list.add(new PeakFilterProcessSupplier<>(filter, this));
		}
		return list;
	}

	public static String getID(IPeakFilter<?> filter) {

		return "PeakFilter:" + filter.getID();
	}

	public class PeakFilterProcessSupplier<ConfigType> extends AbstractProcessSupplier<ConfigType> implements IChromatogramSelectionProcessSupplier<ConfigType> {

		private final IPeakFilter<ConfigType> filter;

		PeakFilterProcessSupplier(IPeakFilter<ConfigType> filter, IProcessTypeSupplier parent) {

			super(getID(filter), filter.getName(), filter.getDescription(), filter.getConfigClass(), parent, filter.getDataCategories());
			this.filter = filter;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, ConfigType processSettings, ProcessExecutionContext context) {

			doFilter(chromatogramSelection, processSettings, context);
			return chromatogramSelection;
		}

		private void doFilter(IChromatogramSelection<?, ?> chromatogramSelection, ConfigType processSettings, ProcessExecutionContext context) {

			filter.filterPeaks(chromatogramSelection, processSettings, context);
		}
	}
}
