/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;

public class NamedScanMSD extends ScanMSD implements INamedScanMSD {

	private static final long serialVersionUID = -6525787873272881014L;
	private String substanceName;
	private String originName;
	private Long originalReferenceMassSpectrumId;

	public NamedScanMSD() {

		super();
	}

	@Override
	public String getOriginName() {

		return originName;
	}

	@Override
	public void setOriginName(String originName) {

		this.originName = originName;
	}

	@Override
	public String getSubstanceName() {

		return substanceName;
	}

	@Override
	public void setSubstanceName(String substanceName) {

		this.substanceName = substanceName;
	}

	@Override
	public Long getOriginalReferenceMassSpectrumId() {

		return originalReferenceMassSpectrumId;
	}

	@Override
	public void setOriginalReferenceMassSpectrumId(Long originalReferenceMassSpectrumId) {

		this.originalReferenceMassSpectrumId = originalReferenceMassSpectrumId;
	}
}
