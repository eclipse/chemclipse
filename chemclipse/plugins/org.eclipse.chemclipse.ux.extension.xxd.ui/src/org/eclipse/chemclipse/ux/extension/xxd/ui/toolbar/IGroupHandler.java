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

import java.util.List;

import org.eclipse.jface.preference.IPreferencePage;

public interface IGroupHandler {

	List<IPreferencePage> getPreferencePages();

	List<IPartHandler> getPartHandler();

	List<IPartHandler> getPartHandlerMandatory();

	List<IPartHandler> getPartHandlerAdditional();

	void activateParts();

	void updateMenu();

	String getPartElementId(IPartHandler partHandler);

	String getSettingsElementId();

	String getImageHide();

	String getImageShow();

	String getName();
}
