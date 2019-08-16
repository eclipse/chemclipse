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
package org.eclipse.chemclipse.model.support;

/**
 * This class defines a start and stop background abundances used to create a
 * peak with PeakBuilder.
 */
public interface IBackgroundAbundanceRange {

	float MIN_BACKGROUND_ABUNDANCE = 0;
	float MAX_BACKGROUND_ABUNDANCE = Float.MAX_VALUE;

	float getStartBackgroundAbundance();

	float getStopBackgroundAbundance();
}
