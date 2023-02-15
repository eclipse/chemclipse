/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.help;

public class HelpContext {

	HelpContext() {

		// static only
	}

	public static final String PREFIX = "org.eclipse.chemclipse.ux.extension.xxd.ui."; //$NON-NLS-1$
	//
	public static final String CHROMATOGRAM_EDITOR = PREFIX + "chromatogram_editor"; //$NON-NLS-1$
	public static final String CHROMATOGRAM_OVERLAY = PREFIX + "chromatogram_overlay"; //$NON-NLS-1$
	public static final String PEAK_SCAN_LIST = PREFIX + "peak_scan_list"; //$NON-NLS-1$
	public static final String TARGETS = PREFIX + "targets"; //$NON-NLS-1$
}
