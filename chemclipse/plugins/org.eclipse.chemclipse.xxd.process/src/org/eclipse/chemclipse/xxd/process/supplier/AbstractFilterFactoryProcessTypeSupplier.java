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
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.ProcessorFactory;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.support.settings.parser.SettingsClassParser;
import org.eclipse.chemclipse.support.settings.parser.SettingsParser;
import org.eclipse.chemclipse.xxd.process.support.IProcessSupplier;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;

public abstract class AbstractFilterFactoryProcessTypeSupplier<DT, FT extends Filter<?>> implements IProcessTypeSupplier {

	private Map<String, FilterProcessSupplier<FT, ?>> suppliers = new HashMap<>();

	public AbstractFilterFactoryProcessTypeSupplier(ProcessorFactory filterFactory) {
	}

	@Override
	public String getCategory() {

		return "Measurement Filter";
	}

	protected <X> FilterProcessSupplier<FT, X> createProcessorSupplier(FT filter) {

		FilterProcessSupplier<FT, X> processorSupplier = new FilterProcessSupplier<FT, X>(filter, this);
		suppliers.put(processorSupplier.getId(), processorSupplier);
		return processorSupplier;
	}

	public static List<DataType> convertDataTypes(DataCategory[] dataCategories) {

		List<DataType> types = new ArrayList<>();
		for(DataCategory dataCategory : dataCategories) {
			switch(dataCategory) {
				case CSD:
					types.add(DataType.CSD);
					break;
				case MSD:
					types.add(DataType.MSD);
					break;
				case WSD:
					types.add(DataType.WSD);
					break;
				case FID:
				case NMR:
					types.add(DataType.NMR);
					break;
				default:
					types.add(DataType.AUTO_DETECT);
					break;
			}
		}
		return types;
	}

	@Override
	public List<IProcessSupplier<?>> getProcessorSuppliers() {

		return new ArrayList<>(suppliers.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> FilterProcessSupplier<FT, X> getProcessorSupplier(String id) {

		return (FilterProcessSupplier<FT, X>)suppliers.get(id);
	}

	protected static class FilterProcessSupplier<FilterType extends Filter<?>, X> implements IProcessSupplier<X> {

		private FilterType filter;
		private Set<DataType> dataTypes;
		private IProcessTypeSupplier parent;
		private SettingsClassParser parser;

		public FilterProcessSupplier(FilterType filter, IProcessTypeSupplier parent) {
			this.filter = filter;
			this.parent = parent;
			dataTypes = Collections.unmodifiableSet(EnumSet.copyOf(convertDataTypes(filter.getDataCategories())));
		}

		@Override
		public String getId() {

			return filter.getID();
		}

		@Override
		public String getName() {

			return filter.getName();
		}

		@Override
		public String getDescription() {

			return filter.getDescription();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<X> getSettingsClass() {

			return (Class<X>)filter.getConfigClass();
		}

		@Override
		public Set<DataType> getSupportedDataTypes() {

			return dataTypes;
		}

		public FilterType getFilter() {

			return filter;
		}

		@Override
		public IProcessTypeSupplier getTypeSupplier() {

			return parent;
		}

		@Override
		public SettingsParser getSettingsParser() {

			if(parser == null) {
				parser = new SettingsClassParser(getSettingsClass());
			}
			return parser;
		}
	}
}