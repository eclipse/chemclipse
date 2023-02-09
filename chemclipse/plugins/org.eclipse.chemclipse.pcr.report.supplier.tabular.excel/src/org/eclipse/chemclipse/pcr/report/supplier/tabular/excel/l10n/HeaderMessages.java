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
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.excel.l10n;

import org.eclipse.osgi.util.NLS;

public class HeaderMessages extends NLS {

	public static String pos;
	public static String name;
	public static String analysis;
	public static String subset;
	public static String results;
	//
	static {
		NLS.initializeMessages("org.eclipse.chemclipse.pcr.report.supplier.tabular.excel.l10n.messages", HeaderMessages.class); //$NON-NLS-1$
	}

	private HeaderMessages() {

	}
}
