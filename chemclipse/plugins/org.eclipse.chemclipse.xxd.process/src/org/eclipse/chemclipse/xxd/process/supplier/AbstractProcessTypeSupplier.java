/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;

public abstract class AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	private List<DataType> supportedDataTypes = new ArrayList<>();

	public AbstractProcessTypeSupplier(DataType[] dataTypes) {
		for(DataType dataType : dataTypes) {
			supportedDataTypes.add(dataType);
		}
	}

	@Override
	public List<DataType> getSupportedDataTypes() {

		return supportedDataTypes;
	}

	@Override
	public Class<? extends IProcessSettings> getProcessSettingsClass(String processorId) throws Exception {

		return null;
	}
}
