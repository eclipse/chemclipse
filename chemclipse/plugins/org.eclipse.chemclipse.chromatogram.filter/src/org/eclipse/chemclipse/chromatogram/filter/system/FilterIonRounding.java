/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.system;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.chromatogram.filter.l10n.Messages;
import org.eclipse.chemclipse.model.math.IonRoundMethod;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.system.AbstractSystemProcessSettings;
import org.eclipse.chemclipse.processing.system.AbstractSystemProcessSupplier;
import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;
import org.eclipse.chemclipse.support.literature.LiteratureReference;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class FilterIonRounding extends AbstractSystemProcessSettings {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.filter.system.ionRounding"; //$NON-NLS-1$
	private static final String FILE_LITERATURE_RIS = "9294.ris";

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractSystemProcessSupplier<SettingsIonRounding> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, Messages.ionRoundMethod, Messages.ionRoundMethodDescription, SettingsIonRounding.class, parent);
			getLiteratureReferences().add(getLiteratureReference());
		}

		@Override
		public void executeUserSettings(ISystemProcessSettings settings, ProcessExecutionContext context) throws Exception {

			if(settings instanceof SettingsIonRounding processSettings) {
				IonRoundMethod.setActive(processSettings.getIonRoundMethod());
			}
		}
	}

	private static LiteratureReference getLiteratureReference() {

		String content;
		try {
			content = new String(FilterIonRounding.class.getResourceAsStream(FILE_LITERATURE_RIS).readAllBytes());
		} catch(IOException e) {
			content = "https://doi.org/10.1002/rcm.9294";
		}
		//
		return new LiteratureReference(content);
	}
}