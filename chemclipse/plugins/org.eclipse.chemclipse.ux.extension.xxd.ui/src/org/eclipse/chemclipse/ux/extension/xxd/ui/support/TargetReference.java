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
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;

public interface TargetReference extends ITargetSupplier {

	/**
	 * 
	 * @return the display name of this reference
	 */
	String getName();

	/**
	 * 
	 * @return a persistent unique ID that could be used to reference this target
	 */
	String getID();

	/**
	 * 
	 * @return the best matching target from this reference
	 */
	default IIdentificationTarget getBestTarget() {

		return IIdentificationTarget.getBestIdentificationTarget(getTargets());
	}
}
