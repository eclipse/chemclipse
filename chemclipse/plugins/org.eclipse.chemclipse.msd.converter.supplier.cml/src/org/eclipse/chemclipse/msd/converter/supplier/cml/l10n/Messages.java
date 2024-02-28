/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.cml.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.chemclipse.msd.converter.supplier.cml.l10n.messages"; //$NON-NLS-1$
	//
	public static String description;
	public static String noMassSpectraStored;
	public static String fileNotFound;
	public static String fileNotReadable;
	public static String fileIsEmpty;
	public static String ioError;
	//
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {

	}
}
