/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - new API and re-implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.l10n;

import org.eclipse.osgi.util.NLS;

public class SupportMessages extends NLS {

	public static String errorMessageOddIncludingZero;
	public static String errorMessageOdd;
	public static String errorMessageEven;
	//
	static {
		NLS.initializeMessages("org.eclipse.chemclipse.support.ui.l10n.messages", SupportMessages.class); //$NON-NLS-1$
	}

	private SupportMessages() {

	}
}
