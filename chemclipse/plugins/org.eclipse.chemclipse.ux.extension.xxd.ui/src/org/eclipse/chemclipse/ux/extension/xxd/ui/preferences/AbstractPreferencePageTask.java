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
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import java.util.List;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.GroupHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.IGroupHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.IPartHandler;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public abstract class AbstractPreferencePageTask extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private IGroupHandler groupHandler;

	public AbstractPreferencePageTask(IGroupHandler groupHandler) {

		super(GRID);
		this.groupHandler = groupHandler;
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle(groupHandler != null ? groupHandler.getName() : "n.a.");
		setDescription("");
	}

	public void createFieldEditors() {

		if(groupHandler != null) {
			addField(new LabelFieldEditor("Mandatory", getFieldEditorParent()));
			List<IPartHandler> partHandlersMandatory = groupHandler.getPartHandlerMandatory();
			for(IPartHandler partHandler : partHandlersMandatory) {
				addField(new ComboFieldEditor(partHandler.getPartStackReference().getStackPositionKey(), partHandler.getName() + ":", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
			}
			//
			List<IPartHandler> partHandlersAdditional = groupHandler.getPartHandlerAdditional();
			if(partHandlersAdditional.size() > 0) {
				addField(new SpacerFieldEditor(getFieldEditorParent()));
				addField(new LabelFieldEditor("Additional", getFieldEditorParent()));
				for(IPartHandler partHandler : partHandlersAdditional) {
					addField(new ComboFieldEditor(partHandler.getPartStackReference().getStackPositionKey(), partHandler.getName() + ":", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
				}
			}
		}
	}

	public void init(IWorkbench workbench) {

	}

	@Override
	public boolean performOk() {

		boolean ok = super.performOk();
		GroupHandler.updateGroupHandlerMenu();
		return ok;
	}
}
