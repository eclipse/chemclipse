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
import java.util.Collection;
import java.util.function.Function;

import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;

public class DataExplorerContentProvider extends LazyFileExplorerContentProvider {

	private Function<File, Collection<ISupplierFileIdentifier>> supplierFunction;

	public DataExplorerContentProvider(Function<File, Collection<ISupplierFileIdentifier>> supplierFunction) {
		this.supplierFunction = supplierFunction;
	}

	@Override
	public boolean accept(File file) {

		if(super.accept(file)) {
			if(file.isDirectory()) {
				return true;
			}
			return !supplierFunction.apply(file).isEmpty();
		}
		return false;
	}
}
