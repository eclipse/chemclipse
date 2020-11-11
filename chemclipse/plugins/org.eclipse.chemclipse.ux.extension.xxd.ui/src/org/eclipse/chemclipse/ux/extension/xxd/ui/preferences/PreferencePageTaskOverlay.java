/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.GroupHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.GroupHandlerOverlay;
import org.eclipse.jface.preference.ComboFieldEditor;

public class PreferencePageTaskOverlay extends AbstractPreferencePageTask {

	public PreferencePageTaskOverlay() {

		super(GroupHandler.getGroupHandler(GroupHandlerOverlay.NAME));
	}

	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceConstants.P_STACK_POSITION_OVERLAY_CHROMATOGRAM_EXTRA, "Overlay Chromatogram Extra:", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		super.createFieldEditors();
	}
}
