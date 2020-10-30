/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactored into support class
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

import java.util.Collection;

import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.provider.LazyFileExplorerContentProvider;
import org.eclipse.chemclipse.xxd.process.files.SupplierFileIdentifierCache;

public class IdentifierCacheSupport {

	public static SupplierFileIdentifierCache createIdentifierCache(Collection<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {

		SupplierFileIdentifierCache cache = new SupplierFileIdentifierCache(LazyFileExplorerContentProvider.MAX_CACHE_SIZE);
		cache.setIdentifier(supplierFileIdentifierList);
		return cache;
	}
}
