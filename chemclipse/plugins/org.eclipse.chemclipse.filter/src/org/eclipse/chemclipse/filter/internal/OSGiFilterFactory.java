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
package org.eclipse.chemclipse.filter.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.filter.Filter;
import org.eclipse.chemclipse.filter.FilterFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * Implementation of the {@link FilterFactory} based on OSGi-Services, this can be used for example in E4 as follows:
 * <pre>
 * 
 * &#64;Inject
 * FilterFactory filterFactory;
 * &#64;PostConstruct
 * void init() {
 *    // ... do something with the factory ...
 * }
 * </pre>
 * or with OSGi-DS
 * <pre>
 * &#64;Reference(unbind="-") 
 * protected void setFilterFactory(FilterFactory factory) {
 * 
 * }
 * </pre>
 * 
 * @author Christoph Läubrich
 *
 */
@Component(immediate = true, service = {FilterFactory.class})
public class OSGiFilterFactory implements FilterFactory {

	private List<FilterDescriptor> filterDescriptors = new CopyOnWriteArrayList<>();

	@Override
	public <FilterType extends Filter<?>> Collection<FilterType> getFilters(Class<FilterType> filterType, BiFunction<FilterType, Map<String, ?>, Boolean> acceptor) {

		ArrayList<FilterType> list = new ArrayList<>();
		for(FilterDescriptor filterDescriptor : filterDescriptors) {
			if(filterType.isInstance(filterDescriptor.filter)) {
				FilterType filter = filterType.cast(filterDescriptor.filter);
				if(acceptor == null || acceptor.apply(filter, filterDescriptor.properties)) {
					list.add(filter);
				}
			}
		}
		Collections.sort(list, new Comparator<Filter<?>>() {

			@Override
			public int compare(Filter<?> f1, Filter<?> f2) {

				return getName(f1).compareToIgnoreCase(getName(f2));
			}

			private String getName(Filter<?> f) {

				String name = f.getFilterName();
				if(name != null) {
					return name;
				}
				return f.getClass().getName();
			}
		});
		return list;
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	protected void addFilter(Filter<?> filter, Map<String, ?> properties) {

		filterDescriptors.add(new FilterDescriptor(filter, properties));
	}

	protected void removeFilter(Filter<?> filter) {

		for(FilterDescriptor filterDescriptor : filterDescriptors) {
			if(filterDescriptor.filter == filter) {
				filterDescriptors.remove(filterDescriptor);
				return;
			}
		}
	}

	private static final class FilterDescriptor {

		private final Filter<?> filter;
		private final Map<String, ?> properties;

		FilterDescriptor(Filter<?> filter, Map<String, ?> properties) {
			this.filter = filter;
			this.properties = new HashMap<>(properties);
		}

		@Override
		public String toString() {

			return "Filter: " + filter + ", Properties: " + properties;
		}
	}
}
