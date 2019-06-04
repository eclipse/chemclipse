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
 * Christoph Läubrich - adopt to new API
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;

public class DataExplorerContentProvider extends LazyFileExplorerContentProvider {

	private List<? extends ISupplierFileIdentifier> supplierFileIdentifierList;

	public DataExplorerContentProvider(ISupplierFileIdentifier supplierFileIdentifier) {
		this(ExplorerListSupport.getSupplierFileIdentifierList(supplierFileIdentifier));
	}

	public DataExplorerContentProvider(List<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {
		this.supplierFileIdentifierList = supplierFileIdentifierList;
	}

	@Override
	public boolean accept(File file) {

		if(super.accept(file)) {
			if(file.isDirectory()) {
				return true;
			}
			for(ISupplierFileIdentifier supplierFileIdentifier : supplierFileIdentifierList) {
				if(supplierFileIdentifier.isSupplierFile(file)) {
					if(supplierFileIdentifier.isMatchMagicNumber(file)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
