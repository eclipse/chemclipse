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
package org.eclipse.chemclipse.ux.extension.xxd.ui.traces;

public class NamedTraceUtil {

	private static final String USERS_CHOICE = "Users Choice";
	private static final String HYDROCARBONS = "Hydrocarbons";
	private static final String FATTY_ACIDS = "Fatty Acids";
	private static final String FAME = "FAME";
	private static final String SOLVENT_TAILING = "Solvent Tailing";
	private static final String COLUMN_BLEED = "Column Bleed";
	//

	public static final String getDefaultTraces() {

		NamedTraces namedTraces = new NamedTraces();
		//
		namedTraces.add(new NamedTrace(USERS_CHOICE, "18 28 32 84 207"));
		namedTraces.add(new NamedTrace(HYDROCARBONS, "57 71 85"));
		namedTraces.add(new NamedTrace(FATTY_ACIDS, "74 84"));
		namedTraces.add(new NamedTrace(FAME, "79 81"));
		namedTraces.add(new NamedTrace(SOLVENT_TAILING, "84"));
		namedTraces.add(new NamedTrace(COLUMN_BLEED, "207"));
		//
		return namedTraces.save();
	}
}
