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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(service = {ProcessSupplierContext.class})
public class OSGiProcessSupplierContext implements ProcessSupplierContext {

	private final ConcurrentMap<String, IProcessSupplier<?>> supplierMap = new ConcurrentHashMap<>();
	private final Set<IProcessTypeSupplier> typeSupplierSet = new ConcurrentSkipListSet<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> IProcessSupplier<T> getSupplier(String id) {

		// fast path first
		IProcessSupplier<T> mappedSupplier = (IProcessSupplier<T>)supplierMap.get(id);
		if(mappedSupplier != null) {
			return mappedSupplier;
		}
		// second try, ask the ProcessTypeSupplier if they has alternative ids
		for(IProcessTypeSupplier typeSupplier : typeSupplierSet) {
			IProcessSupplier<T> supplier = typeSupplier.getSupplier(id);
			if(supplier != null) {
				return supplier;
			}
		}
		return null;
	}

	@Override
	public void visitSupplier(Consumer<? super IProcessSupplier<?>> consumer) {

		supplierMap.values().forEach(consumer);
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void addProcessTypeSupplier(IProcessTypeSupplier typeSupplier) {

		typeSupplierSet.add(typeSupplier);
		for(IProcessSupplier<?> supplier : typeSupplier.getProcessorSuppliers()) {
			IProcessSupplier<?> old = supplierMap.putIfAbsent(supplier.getId(), supplier);
			if(old != null) {
				throw new IllegalArgumentException("There is a duplicate mapping for id " + supplier.getId());
			}
		}
	}

	public void removeProcessTypeSupplier(IProcessTypeSupplier typeSupplier) {

		typeSupplierSet.remove(typeSupplier);
		for(IProcessSupplier<?> supplier : typeSupplier.getProcessorSuppliers()) {
			supplierMap.remove(supplier.getId());
		}
	}
}
