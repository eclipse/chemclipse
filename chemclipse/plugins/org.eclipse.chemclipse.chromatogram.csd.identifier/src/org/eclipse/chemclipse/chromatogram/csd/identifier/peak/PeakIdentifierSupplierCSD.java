/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.identifier.peak;

import org.eclipse.chemclipse.chromatogram.csd.identifier.settings.IPeakIdentifierSettingsCSD;
import org.eclipse.chemclipse.model.identifier.core.AbstractSupplier;

public class PeakIdentifierSupplierCSD extends AbstractSupplier<IPeakIdentifierSettingsCSD> implements IPeakIdentifierSupplierCSD {

	@Override
	public Class<? extends IPeakIdentifierSettingsCSD> getSettingsClass() {

		return getSpecificIdentifierSettingsClass();
	}
}
