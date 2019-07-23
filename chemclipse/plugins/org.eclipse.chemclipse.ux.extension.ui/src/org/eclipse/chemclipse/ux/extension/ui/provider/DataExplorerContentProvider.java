/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adopt to new API, add caching/access of determined {@link ISupplierFileIdentifier}s
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;
import org.eclipse.jface.viewers.Viewer;

public class DataExplorerContentProvider extends LazyFileExplorerContentProvider {

	private FileCache<Collection<ISupplierFileIdentifier>> supplierCache = new FileCache<>();
	private Viewer viewer;
	private ISupplierFileIdentifier[] fileIdentifiers;

	public DataExplorerContentProvider(Collection<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {
		setSupplierFileIdentifier(supplierFileIdentifierList);
	}

	public void setSupplierFileIdentifier(Collection<? extends ISupplierFileIdentifier> supplierFileIdentifier) {

		fileIdentifiers = supplierFileIdentifier.toArray(new ISupplierFileIdentifier[0]);
		if(viewer != null) {
			viewer.refresh();
		}
	}

	@Override
	public boolean accept(File file) {

		if(super.accept(file)) {
			if(file.isDirectory()) {
				return true;
			}
			return !getSupplierFileIdentifier(file).isEmpty();
		}
		return false;
	}

	@Override
	public void inputChanged(Viewer newViewer, Object oldInput, Object newInput) {

		this.viewer = newViewer;
		super.inputChanged(newViewer, oldInput, newInput);
		supplierCache.clear();
	}

	@Override
	public void dispose() {

		supplierCache.clear();
	}

	public Collection<ISupplierFileIdentifier> getSupplierFileIdentifier(File file) {

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

	@Override
	public void refresh(File file) {

		supplierCache.remove(file);
		super.refresh(file);
	}
}
