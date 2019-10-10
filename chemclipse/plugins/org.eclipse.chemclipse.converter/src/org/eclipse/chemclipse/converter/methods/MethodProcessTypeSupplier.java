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
package org.eclipse.chemclipse.converter.methods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(service = {IProcessTypeSupplier.class})
public class MethodProcessTypeSupplier implements IProcessTypeSupplier {

	private final AtomicReference<ProcessSupplierContext> supplierContext = new AtomicReference<>();

	@Override
	public String getCategory() {

		return "User Methods";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> list = new ArrayList<>();
		Collection<IProcessMethod> userMethods = MethodConverter.getUserMethods();
		for(IProcessMethod processMethod : userMethods) {
			list.add(new MethodProcessSupplier(processMethod, this));
		}
		return list;
	}

	@Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
	public void setProcessSupplierContext(ProcessSupplierContext supplierContext) {

		this.supplierContext.set(supplierContext);
	}

	public void unsetProcessSupplierContext(ProcessSupplierContext supplierContext) {

		this.supplierContext.compareAndSet(supplierContext, null);
	}

	private static final class MethodProcessSupplier extends AbstractProcessSupplier<Void> implements ProcessEntryContainer {

		private final IProcessMethod method;

		public MethodProcessSupplier(IProcessMethod method, MethodProcessTypeSupplier parent) {
			super("ProcessMethod." + method.getUUID(), method.getName(), method.getDescription(), null, parent, getDataTypes(method, parent.supplierContext.get()));
			this.method = method;
		}

		@Override
		public MethodProcessTypeSupplier getTypeSupplier() {

			return (MethodProcessTypeSupplier)super.getTypeSupplier();
		}

		@Override
		public Iterator<IProcessEntry> iterator() {

			return method.iterator();
		}

		@Override
		public int getNumberOfEntries() {

			return method.getNumberOfEntries();
		}
	}

	private static DataCategory[] getDataTypes(IProcessMethod method, ProcessSupplierContext context) {

		if(context == null) {
			// we don't know
			return DataCategory.values();
		}
		Set<DataCategory> categories = new HashSet<>();
		for(IProcessEntry entry : method) {
			IProcessSupplier<?> supplier = context.getSupplier(entry.getProcessorId());
			if(supplier != null) {
				categories.addAll(supplier.getSupportedDataTypes());
			}
		}
		return categories.toArray(new DataCategory[0]);
	}
}
