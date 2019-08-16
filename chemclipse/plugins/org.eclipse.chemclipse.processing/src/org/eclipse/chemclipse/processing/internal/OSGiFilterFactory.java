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
package org.eclipse.chemclipse.processing.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiPredicate;

import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.ProcessorFactory;
import org.eclipse.chemclipse.processing.detector.Detector;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * Implementation of the {@link ProcessorFactory} based on OSGi-Services, this can be used for example in E4 as follows:
 * <pre>
 * 
 * &#64;Inject
 * ProcessorFactory filterFactory;
 * &#64;PostConstruct
 * void init() {
 *    // ... do something with the factory ...
 * }
 * </pre>
 * or with OSGi-DS
 * <pre>
 * &#64;Reference(unbind="-") 
 * protected void setProcessorFactory(ProcessorFactory factory) {
 * 
 * }
 * </pre>
 * 
 * @author Christoph Läubrich
 *
 */
@Component(immediate = true, service = {ProcessorFactory.class})
public class OSGiFilterFactory implements ProcessorFactory {

	private List<ProcessorDescriptor> processorDescriptors = new CopyOnWriteArrayList<>();

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	protected void addFilter(Filter<?> filter, Map<String, ?> properties) {

		addProcessor(filter, properties);
	}

	protected void removeFilter(Filter<?> filter) {

		removeProcessor(filter);
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	protected void addDetector(Detector<?> filter, Map<String, ?> properties) {

		addProcessor(filter, properties);
	}

	protected void removeDetector(Detector<?> filter) {

		removeProcessor(filter);
	}

	private synchronized void addProcessor(Processor<?> filter, Map<String, ?> properties) {

		removeProcessor(filter);
		processorDescriptors.add(new ProcessorDescriptor(filter, properties));
	}

	private synchronized void removeProcessor(Processor<?> filter) {

		for(ProcessorDescriptor filterDescriptor : processorDescriptors) {
			if(filterDescriptor.processor == filter) {
				processorDescriptors.remove(filterDescriptor);
				return;
			}
		}
	}

	private synchronized <E> Map<E, Map<String, ?>> getItems(Class<E> classType) {

		Map<E, Map<String, ?>> map = new IdentityHashMap<>();
		for(ProcessorDescriptor descriptor : processorDescriptors) {
			if(classType.isInstance(descriptor.processor)) {
				map.put(classType.cast(descriptor.processor), descriptor.properties);
			}
		}
		return map;
	}

	private static final class ProcessorDescriptor {

		private final Processor<?> processor;
		private final Map<String, ?> properties;

		ProcessorDescriptor(Processor<?> filter, Map<String, ?> properties) {
			this.processor = filter;
			this.properties = new HashMap<>(properties);
		}

		@Override
		public String toString() {

			return "Processor: " + processor + ", Properties: " + properties;
		}
	}

	@Override
	public <T extends Processor<?>> Collection<T> getProcessors(Class<T> processorType, BiPredicate<? super T, Map<String, ?>> acceptor) {

		Map<T, Map<String, ?>> items = getItems(processorType);
		if(acceptor != null) {
			Set<Entry<T, Map<String, ?>>> entrySet = items.entrySet();
			for(Iterator<Entry<T, Map<String, ?>>> iterator = entrySet.iterator(); iterator.hasNext();) {
				Entry<T, Map<String, ?>> entry = iterator.next();
				if(acceptor.test(entry.getKey(), entry.getValue())) {
					continue;
				}
				iterator.remove();
			}
		}
		return items.keySet();
	}
}
