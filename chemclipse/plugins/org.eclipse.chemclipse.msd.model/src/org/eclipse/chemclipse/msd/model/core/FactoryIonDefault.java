/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved.
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
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
	public Ion create(double mz, float abundance) throws AbundanceLimitExceededException, IonLimitExceededException {

		return new Ion(mz).setAbundance(abundance);
	}
}
