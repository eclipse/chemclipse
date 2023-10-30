/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.system;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.system.AbstractSystemProcessSettings;
import org.eclipse.chemclipse.processing.system.AbstractSystemProcessSupplier;
import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.preferences.PreferenceSupplier;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ConverterProcessSupplier extends AbstractSystemProcessSettings {

	private static final String ID = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.system.converter";
	private static final String NAME = "Open Chromatography Binary";
	private static final String DESCRIPTION = "Chromatogram (*.ocb, ...) system settings.";

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractSystemProcessSupplier<ConverterProcessSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, ConverterProcessSettings.class, parent);
		}

		@Override
		public void executeUserSettings(ISystemProcessSettings settings, ProcessExecutionContext context) throws Exception {

			if(settings instanceof ConverterProcessSettings processSettings) {
				PreferenceSupplier.setChromatogramVersionSave(processSettings.getChromatogramVersion().getVersion());
				PreferenceSupplier.setChromatogramCompressionLevel(processSettings.getChromatogramCompressionLevel());
				PreferenceSupplier.setMethodVersionSave(processSettings.getMethodVersion().getVersion());
				PreferenceSupplier.setMethodCompressionLevel(processSettings.getMethodCompressionLevel());
			}
		}
	}
}