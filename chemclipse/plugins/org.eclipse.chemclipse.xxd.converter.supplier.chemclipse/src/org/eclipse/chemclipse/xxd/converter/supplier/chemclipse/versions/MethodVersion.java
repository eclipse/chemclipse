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

public enum MethodVersion implements IFormatVersion {
	V_0001(IFormat.METHOD_VERSION_0001, "Lawrence v1"), //
	V_0002(IFormat.METHOD_VERSION_0002, "Lawrence v2"), //
	V_0003(IFormat.METHOD_VERSION_0003, "Lawrence v3"), //
	V_1400(IFormat.METHOD_VERSION_1400, "Lawrence v4"), //
	V_1401(IFormat.METHOD_VERSION_1401, "Lawrence v5");

	private String version = "";
	private String release = "";

	private MethodVersion(String version, String release) {

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
