/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.io.Serializable;

/**
 * A segment that seems to only contain noise
 * 
 * @author christoph
 *
 */
public interface NoiseSegment extends IAnalysisSegment, Serializable {

	double getNoiseFactor();
}
