/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.core.IMeasurementInfo;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD;
import org.eclipse.chemclipse.ux.extension.ui.provider.AbstractSupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.e4.core.services.events.IEventBroker;

public class ChromatogramEditorSupport extends AbstractSupplierFileEditorSupport implements ISupplierEditorSupport {

	public ChromatogramEditorSupport() {
		super(ChromatogramConverterMSD.getInstance().getChromatogramConverterSupport().getSupplier());
	}

	@Override
	public String getType() {

		return TYPE_MSD;
	}

	@Override
	public boolean openEditor(File file) {

		return openEditor(file, false);
	}

	@Override
	public boolean openEditor(final File file, boolean batch) {

		/*
		 * Check that the selected file or directory is a valid chromatogram.
		 */
		if(isSupplierFile(file) || isSupplierFileDirectory(file)) {
			openEditor(file, null, ChromatogramEditorMSD.ID, ChromatogramEditorMSD.CONTRIBUTION_URI, ChromatogramEditorMSD.ICON_URI, ChromatogramEditorMSD.TOOLTIP, batch);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void openEditor(IMeasurement measurement) {

		openEditor(null, measurement, ChromatogramEditorMSD.ID, ChromatogramEditorMSD.CONTRIBUTION_URI, ChromatogramEditorMSD.ICON_URI, ChromatogramEditorMSD.TOOLTIP);
	}

	@Override
	public void openOverview(final File file) {

		/*
		 * Check that the selected file or directory is a valid chromatogram.
		 */
		if(isSupplierFile(file) || isSupplierFileDirectory(file)) {
			/*
			 * Push an event
			 * IChromatogramEvents.PROPERTY_CHROMATOGRAM_OVERVIEW_FILE
			 */
			IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
			eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE, file);
		}
	}

	@Override
	public void openOverview(IMeasurementInfo measurementInfo) {

		IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
		eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_OVERVIEW, measurementInfo);
	}
}
