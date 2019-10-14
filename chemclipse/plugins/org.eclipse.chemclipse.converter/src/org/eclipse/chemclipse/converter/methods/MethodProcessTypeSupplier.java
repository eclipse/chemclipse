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

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class MethodProcessTypeSupplier implements IProcessTypeSupplier {

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

	private static final class MethodProcessSupplier extends AbstractProcessSupplier<Void> implements ProcessEntryContainer {

		private final IProcessMethod method;

		public MethodProcessSupplier(IProcessMethod method, MethodProcessTypeSupplier parent) {
			super("ProcessMethod." + method.getUUID(), method.getName(), method.getDescription(), null, parent, getDataTypes(method));
			this.method = method;
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

	private static DataCategory[] getDataTypes(IProcessMethod method) {

		if(method.getNumberOfEntries() == 0) {
			return DataCategory.values();
		}
		Set<DataCategory> categories = new HashSet<>();
		for(IProcessEntry entry : method) {
			categories.addAll(entry.getDataCategories());
		}
		return categories.toArray(new DataCategory[0]);
	}
}
