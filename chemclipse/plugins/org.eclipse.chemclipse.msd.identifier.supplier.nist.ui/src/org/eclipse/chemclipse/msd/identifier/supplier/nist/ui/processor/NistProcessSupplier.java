/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.processor;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.IExtendedRuntimeSupport;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.RuntimeSupportFactory;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class NistProcessSupplier implements IProcessTypeSupplier {

	private static final Logger logger = Logger.getLogger(NistProcessSupplier.class);
	//
	private static final String ID = "org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.processor.openNISTDB";
	private static final String NAME = "NIST-DB GUI";
	private static final String DESCRIPTION = "Open the NIST-DB GUI version.";

	@Override
	public String getCategory() {

		return ICategories.EXTERNAL_PROGRAMS;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<NistProcessSettings> implements IChromatogramSelectionProcessSupplier<NistProcessSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, NistProcessSettings.class, parent, DataCategory.MSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, NistProcessSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			NistProcessSettings settings = (processSettings == null) ? new NistProcessSettings() : processSettings;
			File folder = settings.getNistFolder();
			if(folder.exists()) {
				try {
					IExtendedRuntimeSupport runtimeSupport = RuntimeSupportFactory.getRuntimeSupport(folder);
					runtimeSupport.executeOpenCommand();
				} catch(IOException e) {
					logger.warn(e);
				}
			} else {
				logger.warn("MSSEARCH folder doesn't exist: " + folder.getAbsolutePath());
			}
			//
			return chromatogramSelection;
		}
	}
}