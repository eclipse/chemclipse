/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.versions;

import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;

public enum QuantDatabaseVersion implements IFormatVersion {
	V_0001(IFormat.QUANTDB_VERSION_0001, "Dalton");

	private String version = "";
	private String release = "";

	private QuantDatabaseVersion(String version, String release) {

		this.version = version;
		this.release = release;
	}

	@Override
	public String getVersion() {

		return version;
	}

	@Override
	public String getRelease() {

		return release;
	}
}
