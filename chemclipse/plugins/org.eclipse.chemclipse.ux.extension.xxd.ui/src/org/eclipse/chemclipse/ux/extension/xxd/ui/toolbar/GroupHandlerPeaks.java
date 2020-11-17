/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskPeaks;
import org.eclipse.jface.preference.IPreferencePage;

public class GroupHandlerPeaks extends AbstractGroupHandler {

	public static final String NAME = "Peaks";
	//
	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_SELECTED_PEAKS_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_SELECTED_PEAKS_DEFAULT;

	@Override
	public List<IPreferencePage> getPreferencePages() {

		List<IPreferencePage> preferencePages = new ArrayList<>();
		preferencePages.add(new PreferencePageTaskPeaks());
		return preferencePages;
	}

	@Override
	public List<IPartHandler> getPartHandlerMandatory() {

		List<IPartHandler> partHandler = new ArrayList<>();
		//
		partHandler.add(new PartHandler("Peak Chart", PartSupport.PARTDESCRIPTOR_PEAK_CHART, PreferenceConstants.P_STACK_POSITION_PEAK_CHART));
		partHandler.add(new PartHandler("Peak Details", PartSupport.PARTDESCRIPTOR_PEAK_DETAILS, PreferenceConstants.P_STACK_POSITION_PEAK_DETAILS));
		partHandler.add(new PartHandler("Peak Detector", PartSupport.PARTDESCRIPTOR_PEAK_DETECTOR, PreferenceConstants.P_STACK_POSITION_PEAK_DETECTOR));
		partHandler.add(new PartHandler("Peak List", PartSupport.PARTDESCRIPTOR_PEAK_SCAN_LIST, PreferenceConstants.P_STACK_POSITION_PEAK_SCAN_LIST));
		partHandler.add(new PartHandler("Peak Traces", PartSupport.PARTDESCRIPTOR_PEAK_TRACES, PreferenceConstants.P_STACK_POSITION_PEAK_TRACES));
		//
		return partHandler;
	}

	@Override
	public List<IPartHandler> getPartHandlerAdditional() {

		List<IPartHandler> partHandler = new ArrayList<>();
		//
		partHandler.add(new PartHandler("Molecule", PartSupport.PARTDESCRIPTOR_MOLECULE, PreferenceConstants.P_STACK_POSITION_MOLECULE));
		partHandler.add(new PartHandler("Scan Comparison", PartSupport.PARTDESCRIPTOR_COMPARISON_SCAN, PreferenceConstants.P_STACK_POSITION_COMPARISON_SCAN_CHART));
		partHandler.add(new PartHandler("Peak Quantitation List", PartSupport.PARTDESCRIPTOR_PEAK_QUANTITATION_LIST, PreferenceConstants.P_STACK_POSITION_PEAK_QUANTITATION_LIST));
		partHandler.add(new PartHandler("Quantitation", PartSupport.PARTDESCRIPTOR_QUANTITATION, PreferenceConstants.P_STACK_POSITION_QUANTITATION));
		partHandler.add(new PartHandler("Integration", PartSupport.PARTDESCRIPTOR_INTEGRATION_AREA, PreferenceConstants.P_STACK_POSITION_INTEGRATION_AREA));
		//
		return partHandler;
	}

	@Override
	public String getName() {

		return NAME;
	}

	@Override
	public String getImageHide() {

		return IMAGE_HIDE;
	}

	@Override
	public String getImageShow() {

		return IMAGE_SHOW;
	}
}
