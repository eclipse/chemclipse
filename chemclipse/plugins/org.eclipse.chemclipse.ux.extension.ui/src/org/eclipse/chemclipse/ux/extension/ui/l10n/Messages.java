/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - more translations
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.chemclipse.ux.extension.ui.l10n.messages"; //$NON-NLS-1$ //
	//
	public static String drives;
	public static String home;
	public static String openAllContainedMeasurements;
	public static String openAs;
	public static String openSelectedMeasurements;
	public static String scanForFilesystemUpdates;
	public static String selectDirectory;
	public static String selectUserLocation;
	public static String storingPreferencesFailed;
	public static String tryToOpenAllSelectedFiles;
	public static String userLocation;
	public static String workspace;
	public static String setAsMethodDirectory;
	public static String setAsActiveMethod;
	//
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {

	}
}