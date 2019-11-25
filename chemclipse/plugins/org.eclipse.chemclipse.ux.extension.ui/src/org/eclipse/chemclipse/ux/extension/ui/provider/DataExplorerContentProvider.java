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
import java.util.Map;
import java.util.function.Function;

import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;

public class DataExplorerContentProvider extends LazyFileExplorerContentProvider {

	private final Function<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> supplierFunction;

	public DataExplorerContentProvider(Function<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> identifier) {
		this.supplierFunction = identifier;
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
