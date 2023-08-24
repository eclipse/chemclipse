/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.system;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessExecutor;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;

public abstract class AbstractSystemProcessSupplier<S extends ISystemProcessSettings> extends AbstractProcessSupplier<S> implements IProcessExecutor, SystemExecutor {

	public AbstractSystemProcessSupplier(String id, String name, String description, Class<S> settingsClass, IProcessTypeSupplier parent) {

		super(id, name, description, settingsClass, parent, DataCategory.chromatographyCategories());
	}

	public AbstractSystemProcessSupplier(String id, String name, String description, Class<S> settingsClass, IProcessTypeSupplier parent, DataCategory... dataTypes) {

		super(id, name, description, settingsClass, parent, dataTypes);
	}

	@Override
	public <X> void execute(IProcessorPreferences<X> preferences, ProcessExecutionContext context) throws Exception {

		if(!preferences.isUseSystemDefaults()) {
			X settings = preferences.getUserSettings();
			if(settings instanceof ISystemProcessSettings processSettings) {
				executeUserSettings(processSettings, context);
			}
		}
	}
}