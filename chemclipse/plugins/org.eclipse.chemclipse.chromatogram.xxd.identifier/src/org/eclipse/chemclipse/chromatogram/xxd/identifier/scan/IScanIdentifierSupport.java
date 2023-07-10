/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.scan;

import java.util.Collection;

import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.identifier.core.ISupport;

public interface IScanIdentifierSupport extends ISupport {

	@Override
	IScanIdentifierSupplier getIdentifierSupplier(String identifierId) throws NoIdentifierAvailableException;

	Collection<IScanIdentifierSupplier> getSuppliers();
}