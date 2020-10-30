/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.files;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;

public class SupplierFileIdentifierCache implements Function<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> {

	private ISupplierFileIdentifier[] fileIdentifiers = new ISupplierFileIdentifier[0];
	private Map<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> supplierCache;

	public SupplierFileIdentifierCache(int maxFileSize) {

		supplierCache = new LinkedHashMap<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>>(maxFileSize, 0.75f, true) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean removeEldestEntry(Map.Entry<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> eldest) {

				return size() > maxFileSize;
			}
		};
	}

	public void setIdentifier(Collection<? extends ISupplierFileIdentifier> supplierFileIdentifier) {

		fileIdentifiers = supplierFileIdentifier.toArray(new ISupplierFileIdentifier[0]);
		refreshAll();
	}

	@Override
	public Map<ISupplierFileIdentifier, Collection<ISupplier>> apply(File file) {

		Map<ISupplierFileIdentifier, Collection<ISupplier>> list = supplierCache.get(file);
		//
		if(list == null) {
			list = new LinkedHashMap<>();
			for(ISupplierFileIdentifier supplierFileIdentifier : fileIdentifiers) {
				Collection<ISupplier> supplier = supplierFileIdentifier.getSupplier(file);
				if(!supplier.isEmpty()) {
					list.put(supplierFileIdentifier, Collections.unmodifiableCollection(supplier));
				}
			}
			list = Collections.unmodifiableMap(list);
			supplierCache.put(file, list);
		}
		return list;
	}

	public void refreshAll() {

		supplierCache.clear();
	}

	public void refresh(File file) {

		supplierCache.remove(file);
	}
}
