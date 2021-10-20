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
package org.eclipse.chemclipse.msd.converter.supplier.amdis.system;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.system.AbstractSystemProcessSettings;
import org.eclipse.chemclipse.processing.system.AbstractSystemProcessSupplier;
import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ConverterProcessor extends AbstractSystemProcessSettings {

	private static final String ID = "org.eclipse.chemclipse.msd.converter.supplier.amdis.system.converterProcessor";
	private static final String NAME = "AMDIS Converter";
	private static final String DESCRIPTION = "Define the AMDIS converter settings.";

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractSystemProcessSupplier<ConverterSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, ConverterSettings.class, parent);
		}

		@Override
		public void executeUserSettings(ISystemProcessSettings settings, ProcessExecutionContext context) throws Exception {

			if(settings instanceof ConverterSettings) {
				ConverterSettings processSettings = (ConverterSettings)settings;
				PreferenceSupplier.setCharsetImportMSL(processSettings.getCharsetImportMSL());
				PreferenceSupplier.setCharsetImportMSP(processSettings.getCharsetImportMSP());
				PreferenceSupplier.setCharsetImportFIN(processSettings.getCharsetImportFIN());
				PreferenceSupplier.setCharsetImportELU(processSettings.getCharsetImportELU());
			}
		}
	}
}