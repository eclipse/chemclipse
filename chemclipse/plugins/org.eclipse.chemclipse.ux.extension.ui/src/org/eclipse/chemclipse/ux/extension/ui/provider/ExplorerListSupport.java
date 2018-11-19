/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;

public class ExplorerListSupport {

	/**
	 * Use static methods only.
	 */
	private ExplorerListSupport() {
	}

	public static List<ISupplierFileIdentifier> getSupplierFileIdentifierList(ISupplierFileIdentifier supplierFileIdentifier) {

		List<ISupplierFileIdentifier> list = new ArrayList<ISupplierFileIdentifier>();
		list.add(supplierFileIdentifier);
		return list;
	}
}
