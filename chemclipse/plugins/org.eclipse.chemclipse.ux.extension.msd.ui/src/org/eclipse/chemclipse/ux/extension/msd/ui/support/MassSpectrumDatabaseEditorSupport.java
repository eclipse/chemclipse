/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.support;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.MassSpectrumLibraryEditor;
import org.eclipse.chemclipse.ux.extension.ui.provider.AbstractSupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.e4.core.services.events.IEventBroker;

public class MassSpectrumDatabaseEditorSupport extends AbstractSupplierFileEditorSupport implements ISupplierFileEditorSupport {

	public MassSpectrumDatabaseEditorSupport() {
		super(MassSpectrumConverter.getMassSpectrumConverterSupport().getSupplier());
	}

	@Override
	public String getType() {

		return TYPE_DATABASE_MSD;
	}

	@Override
	public void openEditor(final File file) {

		/*
		 * Check that the selected file or directory is a valid database.
		 */
		if(isSupplierFile(file) || isSupplierFileDirectory(file)) {
			openEditor(file, null, MassSpectrumLibraryEditor.ID, MassSpectrumLibraryEditor.CONTRIBUTION_URI, MassSpectrumLibraryEditor.ICON_URI, MassSpectrumLibraryEditor.TOOLTIP);
		}
	}

	@Override
	public void openOverview(final File file) {

		/*
		 * Check that the selected file or directory is a valid database.
		 */
		if(isSupplierFile(file) || isSupplierFileDirectory(file)) {
			/*
			 * Push an event
			 */
			IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
			eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_MASSSPECTRA, file);
		}
	}
}
