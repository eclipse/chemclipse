/*******************************************************************************
 * Copyright (c) 2010, 2019 Lablicate GmbH.
 *
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Jan Holy - implementation
 * Christoph Läubrich - add getSupplier method
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum;

import java.util.Collection;

import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.identifier.core.ISupport;

public interface IMassSpectrumIdentifierSupport extends ISupport {

	@Override
	IMassSpectrumIdentifierSupplier getIdentifierSupplier(String identifierId) throws NoIdentifierAvailableException;

	Collection<IMassSpectrumIdentifierSupplier> getSuppliers();
}
