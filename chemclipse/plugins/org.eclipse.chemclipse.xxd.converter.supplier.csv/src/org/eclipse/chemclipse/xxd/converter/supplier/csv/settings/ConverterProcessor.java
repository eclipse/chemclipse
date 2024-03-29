/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.csv.settings;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.system.AbstractSystemProcessSettings;
import org.eclipse.chemclipse.processing.system.AbstractSystemProcessSupplier;
import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;
import org.eclipse.chemclipse.xxd.converter.supplier.csv.preferences.PreferenceSupplier;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ConverterProcessor extends AbstractSystemProcessSettings {

	/*
	 * Keep ID for legacy reasons.
	 */
	private static final String ID = "org.eclipse.chemclipse.msd.converter.supplier.csv.system.converterProcessor";
	private static final String NAME = "CSV Converter";
	private static final String DESCRIPTION = "CSV converter preferences.";

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

			if(settings instanceof ConverterSettings processSettings) {
				PreferenceSupplier.setImportDelimiter(processSettings.getImportDelimiter());
				PreferenceSupplier.setImportZeroMarker(processSettings.getImportZeroMarker());
				PreferenceSupplier.setExportUseTic(processSettings.isExportUseTic());
				PreferenceSupplier.setExportReferences(processSettings.isExportReferences());
			}
		}
	}
}