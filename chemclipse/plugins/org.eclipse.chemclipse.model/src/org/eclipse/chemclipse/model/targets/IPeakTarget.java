/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.targets;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;

/**
 * @author eselmeister
 */
public interface IPeakTarget extends IIdentificationTarget {

	/**
	 * Returns the parent peak of the actual target.<br/>
	 * The value could be also null.
	 * 
	 * @return {@link IPeak}
	 */
	IPeak getParentPeak();

	/**
	 * Sets the parent peak.
	 * 
	 * @param parentPeak
	 */
	void setParentPeak(IPeak parentPeak);
}
