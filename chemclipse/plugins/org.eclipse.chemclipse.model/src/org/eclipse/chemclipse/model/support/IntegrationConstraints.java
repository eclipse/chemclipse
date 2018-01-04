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

import java.util.HashSet;
import java.util.Set;

/**
 * @author eselmeister
 */
public class IntegrationConstraints implements IIntegrationConstraints {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 7698157179435806626L;
	//
	private Set<IntegrationConstraint> integrationConstraints;

	public IntegrationConstraints() {
		integrationConstraints = new HashSet<IntegrationConstraint>();
	}

	@Override
	public void add(IntegrationConstraint integrationConstraint) {

		integrationConstraints.add(integrationConstraint);
	}

	@Override
	public boolean hasIntegrationConstraint(IntegrationConstraint integrationConstraint) {

		return integrationConstraints.contains(integrationConstraint);
	}

	@Override
	public void remove(IntegrationConstraint integrationConstraint) {

		integrationConstraints.remove(integrationConstraint);
	}

	@Override
	public void removeAll() {

		integrationConstraints.clear();
	}
}
