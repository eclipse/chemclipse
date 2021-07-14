/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
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
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutor;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;

public abstract class AbstractSystemProcessSupplier<S extends ISystemProcessSettings> extends AbstractProcessSupplier<S> implements ProcessExecutor, SystemExecutor {

	public AbstractSystemProcessSupplier(String id, String name, String description, Class<S> settingsClass, IProcessTypeSupplier parent) {

		super(id, name, description, settingsClass, parent, DataCategory.CSD, DataCategory.MSD, DataCategory.WSD);
	}

	@Override
	public <X> void execute(ProcessorPreferences<X> preferences, ProcessExecutionContext context) throws Exception {

		if(!preferences.isUseSystemDefaults()) {
			X settings = preferences.getUserSettings();
			if(settings instanceof ISystemProcessSettings) {
				executeUserSettings((ISystemProcessSettings)settings, context);
			}
		}
	}
}