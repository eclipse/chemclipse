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

import org.eclipse.chemclipse.model.settings.IProcessSettings;

public interface IIntegrationSettings extends IProcessSettings {

	/**
	 * Returns the {@link IBaselineSupport} instance.
	 * 
	 * @return {@link IBaselineSupport}
	 */
	IBaselineSupport getBaselineSupport();
}
