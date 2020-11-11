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
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskESTD;
import org.eclipse.jface.preference.IPreferencePage;

public class GroupHandlerESTD extends AbstractGroupHandler {

	public static final String NAME = "ESTD";
	//
	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_EXTERNAL_STANDARDS_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_EXTERNAL_STANDARDS_DEFAULT;

	@Override
	public List<IPreferencePage> getPreferencePages() {

		List<IPreferencePage> preferencePages = new ArrayList<>();
		preferencePages.add(new PreferencePageTaskESTD());
		return preferencePages;
	}

	@Override
	public List<IPartHandler> getPartHandlerMandatory() {

		List<IPartHandler> partHandler = new ArrayList<>();
		//
		partHandler.add(new PartHandler("Quant Peak List", PartSupport.PARTDESCRIPTOR_QUANT_PEAKS_LIST, PreferenceConstants.P_STACK_POSITION_QUANT_PEAKS_LIST));
		partHandler.add(new PartHandler("Quant Peak Chart", PartSupport.PARTDESCRIPTOR_QUANT_PEAKS_CHART, PreferenceConstants.P_STACK_POSITION_QUANT_PEAKS_CHART));
		partHandler.add(new PartHandler("Quant Signals List", PartSupport.PARTDESCRIPTOR_QUANT_SIGNALS_LIST, PreferenceConstants.P_STACK_POSITION_QUANT_SIGNALS_LIST));
		partHandler.add(new PartHandler("Quant Response List", PartSupport.PARTDESCRIPTOR_QUANT_RESPONSE_LIST, PreferenceConstants.P_STACK_POSITION_QUANT_RESPONSE_LIST));
		partHandler.add(new PartHandler("Quant Response Chart", PartSupport.PARTDESCRIPTOR_QUANT_RESPONSE_CHART, PreferenceConstants.P_STACK_POSITION_QUANT_RESPONSE_CHART));
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
