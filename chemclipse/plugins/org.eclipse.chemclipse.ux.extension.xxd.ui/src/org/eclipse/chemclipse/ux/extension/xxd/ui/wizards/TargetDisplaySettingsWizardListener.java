/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import java.util.function.Predicate;

import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetReference;

public interface TargetDisplaySettingsWizardListener {

	String getIDLabel();

	/**
	 * Set the preview settings
	 * 
	 * @param settings
	 *            the settings to use for preview or <code>null</code> if no preview is desired
	 * @param viewerFilters
	 */
	void setPreviewSettings(TargetDisplaySettings settings, Predicate<TargetReference> filter);

	boolean isShowPreview();

	void setShowPreview(boolean preview);
}
