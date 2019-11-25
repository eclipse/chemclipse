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

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.converter.AbstractSupplierFileIdentifier;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;

public class ProjectExplorerFileIdentifier extends AbstractSupplierFileIdentifier implements ISupplierFileIdentifier {

	private String type = "";

	public ProjectExplorerFileIdentifier(DataType dataType) {
		super(getSupplier(dataType));
		initialize(dataType);
	}

	private static List<ISupplier> getSupplier(DataType dataType) {

		List<ISupplier> supplier = new ArrayList<ISupplier>();
		switch(dataType) {
			case CAL:
				supplier.add(new CalibrationFileSupplier());
				break;
			default:
				// No action
		}
		//
		return supplier;
	}

	private void initialize(DataType dataType) {

		switch(dataType) {
			case CAL:
				type = TYPE_CAL;
				break;
			default:
				type = "";
		}
	}

	@Override
	public String getType() {

		return type;
	}
}
