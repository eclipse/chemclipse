/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings;

import org.eclipse.chemclipse.model.settings.AbstractProcessSettings;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractIntegrationSettings extends AbstractProcessSettings implements IIntegrationSettings {

	@JsonIgnore
	private IBaselineSupport baselineSupport;

	public AbstractIntegrationSettings() {
		baselineSupport = new BaselineSupport();
	}

	@Override
	public IBaselineSupport getBaselineSupport() {

		return baselineSupport;
	}
}
