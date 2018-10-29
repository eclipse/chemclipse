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
package org.eclipse.chemclipse.model.targets;

import java.io.Serializable;

/**
 * A target is used to identify a peak or a chromatogram. This can be done in
 * various ways.<br/>
 * A peak can be identified for example by a mass spectral library search
 * (INCOS, PBM ...) or it could be determined, which origin the mass spectrum
 * has.<br/>
 * And there are still a lot more. The peak identification is implemented as an
 * extension point. So, several different identification methods can be
 * developed if needed.<br/>
 * The chromatogram identification can also be done in several ways.<br/>
 * It can be identified by a summed mass spectrum like in F-Search, or it could
 * be identified by several of its components. Feel free, to implement something
 * :-).
 */
public interface ITarget extends Serializable {
}
