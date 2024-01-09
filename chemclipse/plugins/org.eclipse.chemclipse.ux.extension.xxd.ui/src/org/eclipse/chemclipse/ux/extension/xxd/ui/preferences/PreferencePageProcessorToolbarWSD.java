/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;

public class PreferencePageProcessorToolbarWSD extends AbstractPreferencePageToolbar {

	public PreferencePageProcessorToolbarWSD() {

		super(Activator.getDefault().getPreferenceStore(), PreferenceSupplier.P_QUICK_ACCESS_PROCESSORS, DataCategory.WSD);
	}
}
