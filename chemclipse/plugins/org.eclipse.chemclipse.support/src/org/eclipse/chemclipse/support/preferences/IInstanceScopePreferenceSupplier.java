/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public interface IInstanceScopePreferenceSupplier extends IPreferenceSupplier {

	@Override
	default IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	default IEclipsePreferences getPreferences() {

		// TODO: should be one up in hierarchy
		return getScopeContext().getNode(getPreferenceNode());
	}
}
