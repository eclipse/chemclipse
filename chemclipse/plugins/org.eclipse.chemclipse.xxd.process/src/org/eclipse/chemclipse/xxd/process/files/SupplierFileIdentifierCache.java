/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;

public class SupplierFileIdentifierCache implements Function<File, Collection<ISupplierFileIdentifier>> {

	private ISupplierFileIdentifier[] fileIdentifiers = new ISupplierFileIdentifier[0];
	private Map<File, Collection<ISupplierFileIdentifier>> supplierCache;

	public SupplierFileIdentifierCache(int maxFileSize) {
		supplierCache = new LinkedHashMap<File, Collection<ISupplierFileIdentifier>>(maxFileSize, 0.75f, true) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean removeEldestEntry(Map.Entry<File, Collection<ISupplierFileIdentifier>> eldest) {

				return size() > maxFileSize;
			}
		};
	}

	public void setIdentifier(Collection<? extends ISupplierFileIdentifier> supplierFileIdentifier) {

		fileIdentifiers = supplierFileIdentifier.toArray(new ISupplierFileIdentifier[0]);
		refreshAll();
	}

	@Override
	public Collection<ISupplierFileIdentifier> apply(File file) {

		Collection<ISupplierFileIdentifier> list = supplierCache.get(file);
		if(list == null) {
			list = new ArrayList<>(1);
			if(file.isDirectory()) {
				for(ISupplierFileIdentifier supplierFileIdentifier : fileIdentifiers) {
					if(supplierFileIdentifier.isSupplierFileDirectory(file)) {
						if(supplierFileIdentifier.isMatchMagicNumber(file)) {
							list.add(supplierFileIdentifier);
						}
					}
				}
			} else if(file.isFile()) {
				for(ISupplierFileIdentifier supplierFileIdentifier : fileIdentifiers) {
					if(supplierFileIdentifier.isSupplierFile(file)) {
						if(supplierFileIdentifier.isMatchMagicNumber(file)) {
							list.add(supplierFileIdentifier);
						}
					}
				}
			}
			list = Collections.unmodifiableCollection(list);
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
