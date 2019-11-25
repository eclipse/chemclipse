/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.peak;

import java.util.Collection;

import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.SupplierContext;
import org.osgi.service.component.annotations.Component;

@Component(service = {SupplierContext.class})
public class PeakConverterMSDSupplierContext implements SupplierContext {

	@Override
	public Collection<ISupplier> getSupplier() {

		return PeakConverterMSD.getPeakConverterSupport().getSupplier();
	}
}
