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
 * Philip Wenig - refactorings
 *******************************************************************************/
package org.eclipse.chemclipse.model.supplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.filter.IMeasurementFilter;
import org.eclipse.chemclipse.processing.ProcessorFactory;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = {IProcessTypeSupplier.class})
public class IMeasurementFilterProcessTypeSupplier implements IProcessTypeSupplier {

	private ProcessorFactory processorFactory;

	@Override
	public String getCategory() {

		return "Filter";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> list = new ArrayList<>();
		Collection<IMeasurementFilter<?>> filters = processorFactory.getProcessors(ProcessorFactory.genericClass(IMeasurementFilter.class), null);
		for(IMeasurementFilter<?> filter : filters) {
			list.add(new MeasurementFilterProcessSupplier<>(filter, this));
		}
		return list;
	}

	@Reference(unbind = "-")
	public void setProcessorFactory(ProcessorFactory factory) {

		processorFactory = factory;
	}

	public static String getID(IMeasurementFilter<?> filter) {

		return "MeasurementFilter:" + filter.getID();
	}

	private static final class MeasurementFilterProcessSupplier<SettingsClass> extends AbstractProcessSupplier<SettingsClass> implements IMeasurementProcessSupplier<SettingsClass> {

		private final IMeasurementFilter<SettingsClass> filter;

		public MeasurementFilterProcessSupplier(IMeasurementFilter<SettingsClass> filter, IProcessTypeSupplier parent) {

			super(getID(filter), filter.getName(), filter.getDescription(), filter.getConfigClass(), parent, filter.getDataCategories());
			this.filter = filter;
		}

		@Override
		public Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, SettingsClass processSettings, ProcessExecutionContext context) {

			// first try to process all at once
			if(filter.acceptsIMeasurements(measurements)) {
				return filter.filterIMeasurements(measurements, processSettings, Function.identity(), context, context.getProgressMonitor());
			} else {
				// try individual ones
				List<IMeasurement> resultList = new ArrayList<>();
				SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), measurements.size());
				int unprocessed = 0;
				for(IMeasurement measurement : measurements) {
					Set<IMeasurement> singleItem = Collections.singleton(measurement);
					if(filter.acceptsIMeasurements(singleItem)) {
						// we can process this single item ...
						resultList.addAll(filter.filterIMeasurements(singleItem, processSettings, Function.identity(), context, subMonitor.split(1)));
					} else {
						// we can't process this item either...
						resultList.add(measurement);
						subMonitor.worked(1);
						context.addWarnMessage(getName(), "measurement " + measurement.getDataName() + " is incompatible with this filter");
						unprocessed++;
					}
				}
				if(unprocessed > 0) {
					context.addWarnMessage(getName(), unprocessed + " measurements are skipped because they are incompatible with this filter");
				}
				return resultList;
			}
		}
	}
}
