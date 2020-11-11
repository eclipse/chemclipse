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
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskISTD;
import org.eclipse.jface.preference.IPreferencePage;

public class GroupHandlerISTD extends AbstractGroupHandler {

	public static final String NAME = "ISTD";
	//
	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_INTERNAL_STANDARDS_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_INTERNAL_STANDARDS_DEFAULT;

	@Override
	public List<IPreferencePage> getPreferencePages() {

		List<IPreferencePage> preferencePages = new ArrayList<>();
		preferencePages.add(new PreferencePageTaskISTD());
		return preferencePages;
	}

	@Override
	public List<IPartHandler> getPartHandlerMandatory() {

		List<IPartHandler> partHandler = new ArrayList<>();
		//
		partHandler.add(new PartHandler("Internal Standards", PartSupport.PARTDESCRIPTOR_INTERNAL_STANDARDS, PreferenceConstants.P_STACK_POSITION_INTERNAL_STANDARDS));
		partHandler.add(new PartHandler("Quantitation References", PartSupport.PARTDESCRIPTOR_PEAK_QUANTITATION_REFERENCES, PreferenceConstants.P_STACK_POSITION_PEAK_QUANTITATION_REFERENCES));
		//
		return partHandler;
	}

	@Override
	public List<IPartHandler> getPartHandlerAdditional() {

		List<IPartHandler> partHandler = new ArrayList<>();
		//
		partHandler.add(new PartHandler("Quantitation", PartSupport.PARTDESCRIPTOR_QUANTITATION, PreferenceConstants.P_STACK_POSITION_QUANTITATION));
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
