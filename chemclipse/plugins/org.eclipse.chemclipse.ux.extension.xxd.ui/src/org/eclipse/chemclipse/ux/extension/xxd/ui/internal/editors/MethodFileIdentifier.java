/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.ux.extension.ui.provider.AbstractSupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileIdentifier;

public class MethodFileIdentifier extends AbstractSupplierFileIdentifier implements ISupplierFileIdentifier {

	public MethodFileIdentifier() {
		super(getSupplier());
	}

	private static List<ISupplier> getSupplier() {

		List<ISupplier> supplier = new ArrayList<ISupplier>();
		supplier.add(new MethodFileSupplier());
		return supplier;
	}

	@Override
	public String getType() {

		return TYPE_MTH;
	}
}
