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
 * Jan Holy - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.chromatogram;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IChromatogramIdentifierSettings;
import org.eclipse.chemclipse.model.identifier.core.AbstractSupplier;

public class ChromatogramIdentifierSupplier extends AbstractSupplier<IChromatogramIdentifierSettings> implements IChromatogramIdentifierSupplier {

	@Override
	public Class<? extends IChromatogramIdentifierSettings> getSettingsClass() {

		return getSpecificIdentifierSettingsClass();
	}
}
