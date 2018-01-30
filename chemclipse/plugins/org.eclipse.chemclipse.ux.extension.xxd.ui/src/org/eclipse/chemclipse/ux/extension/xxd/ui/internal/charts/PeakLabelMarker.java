/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakDataSupport;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.marker.LabelMarker;
import org.eclipse.swt.SWT;

public class PeakLabelMarker extends LabelMarker {

	public PeakLabelMarker(BaseChart baseChart, int indexSeries, List<? extends IPeak> peaks) {
		super(baseChart);
		//
		PeakDataSupport peakSupport = new PeakDataSupport();
		List<String> labels = new ArrayList<String>();
		for(IPeak peak : peaks) {
			ILibraryInformation libraryInformation = peakSupport.getLibraryInformation(new ArrayList<IPeakTarget>(peak.getTargets()));
			String substance = "";
			if(libraryInformation != null) {
				substance = libraryInformation.getName();
			}
			labels.add(substance);
		}
		setLabels(labels, indexSeries, SWT.VERTICAL);
	}
}
