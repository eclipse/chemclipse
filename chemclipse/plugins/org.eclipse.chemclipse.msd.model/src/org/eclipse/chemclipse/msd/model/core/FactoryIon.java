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

/**
 * A factory to create {@link IIon ions}.
 *
 * @author <a href="mailto:alexander.kerner@openchrom.net">Alexander Kerner</a>
 *
 */
public interface FactoryIon {

	/**
	 * Creates a new {@link IIon} with given values for <i>mz</i> and
	 * <i>abundance</i>.
	 *
	 * @param mz
	 *            the new ion's mass-to-charge ratio
	 * @param abundance
	 *            the new ion's abundance/ intensity
	 * @return the newly created ion
	 * @throws AbundanceLimitExceededException
	 *             if the given abundance exceeds the abuncance capabilities of
	 *             the implementing type
	 * @throws IonLimitExceededException
	 *             if the given <i>m/z</i> exceeds the <i>m/z</i> capabilities
	 *             of the implementing tpye
	 */
	IIon create(double mz, float abundance) throws AbundanceLimitExceededException, IonLimitExceededException;
}
