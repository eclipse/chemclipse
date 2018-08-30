/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Jan Holy - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.library;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.ILibraryServiceSettings;
import org.eclipse.chemclipse.model.identifier.core.ISupplier;

public interface ILibraryServiceSupplier extends ISupplier {

	@Override
	Class<? extends ILibraryServiceSettings> getIdentifierSettingsClass();
}
