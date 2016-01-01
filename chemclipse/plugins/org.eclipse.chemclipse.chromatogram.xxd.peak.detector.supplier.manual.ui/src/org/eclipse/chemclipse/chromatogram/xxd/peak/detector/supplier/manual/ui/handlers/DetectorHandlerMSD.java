/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;

import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;

public class DetectorHandlerMSD {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		/*
		 * Try to select and show the perspective and view.
		 */
		String perspectiveId = "org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.perspective";
		List<String> viewIds = new ArrayList<String>();
		viewIds.add("org.eclipse.chemclipse.ux.extension.msd.ui.part.peakListView");
		viewIds.add("org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.views.manualDetectedPeakMSD");
		viewIds.add("org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.views.chromatogramSelectionView");
		PerspectiveSwitchHandler.focusPerspectiveAndView(perspectiveId, viewIds);
	}
}
