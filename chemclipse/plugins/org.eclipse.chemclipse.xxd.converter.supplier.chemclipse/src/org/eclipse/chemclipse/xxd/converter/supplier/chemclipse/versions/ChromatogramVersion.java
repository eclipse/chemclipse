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

/*
 * Ensure backward and forward compatibility!
 */
public enum ChromatogramVersion implements IFormatVersion {
	V_0701(IFormat.CHROMATOGRAM_VERSION_0701, "Nernst"), //
	V_0803(IFormat.CHROMATOGRAM_VERSION_0803, "Dempster"), //
	V_0903(IFormat.CHROMATOGRAM_VERSION_0903, "Mattauch"), //
	V_1004(IFormat.CHROMATOGRAM_VERSION_1004, "Aston"), //
	V_1100(IFormat.CHROMATOGRAM_VERSION_1100, "Diels"), //
	V_1300(IFormat.CHROMATOGRAM_VERSION_1300, "Dalton v1"), //
	V_1301(IFormat.CHROMATOGRAM_VERSION_1301, "Dalton v2"), //
	V_1400(IFormat.CHROMATOGRAM_VERSION_1400, "Lawrence"); //

	private String version = "";
	private String release = "";

	private ChromatogramVersion(String version, String release) {

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
