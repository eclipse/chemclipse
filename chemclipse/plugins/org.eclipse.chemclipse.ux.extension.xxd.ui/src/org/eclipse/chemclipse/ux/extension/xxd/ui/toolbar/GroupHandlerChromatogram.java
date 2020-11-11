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
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskChromatogram;
import org.eclipse.jface.preference.IPreferencePage;

public class GroupHandlerChromatogram extends AbstractGroupHandler {

	public static final String NAME = "Chromatogram";
	//
	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_CHROMATOGRAM_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_CHROMATOGRAM_DEFAULT;

	@Override
	public List<IPreferencePage> getPreferencePages() {

		List<IPreferencePage> preferencePages = new ArrayList<>();
		preferencePages.add(new PreferencePageTaskChromatogram());
		return preferencePages;
	}

	@Override
	public List<IPartHandler> getPartHandlerMandatory() {

		List<IPartHandler> partHandler = new ArrayList<>();
		//
		partHandler.add(new PartHandler("Baseline", PartSupport.PARTDESCRIPTOR_BASELINE, PreferenceConstants.P_STACK_POSITION_BASELINE_CHROMATOGRAM));
		partHandler.add(new PartHandler("Heatmap", PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_HEATMAP, PreferenceConstants.P_STACK_POSITION_CHROMATOGRAM_HEATMAP));
		partHandler.add(new PartHandler("Scan Info", PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_SCAN_INFO, PreferenceConstants.P_STACK_POSITION_CHROMATOGRAM_SCAN_INFO));
		partHandler.add(new PartHandler("Integration", PartSupport.PARTDESCRIPTOR_INTEGRATION_AREA, PreferenceConstants.P_STACK_POSITION_INTEGRATION_AREA));
		//
		return partHandler;
	}

	@Override
	public List<IPartHandler> getPartHandlerAdditional() {

		List<IPartHandler> partHandler = new ArrayList<>();
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
