/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - support for sorting / icons
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;

public class PreferencePageProcessorToolbarCSD extends AbstractPreferencePageToolbar {

	public PreferencePageProcessorToolbarCSD() {

		super(Activator.getDefault().getPreferenceStore(), PreferenceConstants.P_QUICK_ACCESS_PROCESSORS_MSD, DataCategory.MSD);
	}
}