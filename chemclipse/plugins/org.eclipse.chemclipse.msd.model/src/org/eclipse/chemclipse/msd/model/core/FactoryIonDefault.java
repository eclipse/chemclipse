/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
 *
 * All rights reserved.
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.msd.model.implementation.Ion;

/**
 * Default implementation for {@link FactoryIon}.
 * Newly created instances are of type {@link Ion}.
 *
 * @author <a href="mailto:alexander.kerner@openchrom.net">Alexander Kerner</a>
 *
 */
public class FactoryIonDefault implements FactoryIon {

	@Override
	public IIon create(double mz, float abundance) {

		return new Ion(mz, abundance);
	}
}
