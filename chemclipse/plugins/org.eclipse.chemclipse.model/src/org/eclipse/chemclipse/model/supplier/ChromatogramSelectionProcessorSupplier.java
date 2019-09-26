/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.supplier;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;

public abstract class ChromatogramSelectionProcessorSupplier<SettingsClass> extends AbstractProcessSupplier<SettingsClass> implements IChromatogramSelectionProcessSupplier<SettingsClass> {

	public ChromatogramSelectionProcessorSupplier(String id, String name, String description, Class<SettingsClass> settingsClass, IProcessTypeSupplier parent, DataType... dataTypes) {
		super(id, name, description, settingsClass, parent, convert(dataTypes));
	}

	public ChromatogramSelectionProcessorSupplier(String id, String name, String description, Class<SettingsClass> settingsClass, IProcessTypeSupplier parent, DataCategory... dataTypes) {
		super(id, name, description, settingsClass, parent, dataTypes);
	}

	private static DataCategory[] convert(DataType[] dataTypes) {

		Set<DataCategory> converted = new HashSet<>();
		for(DataType dataType : dataTypes) {
			converted.add(dataType.toDataCategory());
		}
		return converted.toArray(new DataCategory[0]);
	}
}
