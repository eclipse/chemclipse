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
package org.eclipse.chemclipse.model.support;

/**
 * @author eselmeister
 */
public interface IIntegrationConstraints {

	/**
	 * Add an integration constraint to the list.
	 * 
	 * @param integrationConstraint
	 */
	void add(IntegrationConstraint integrationConstraint);

	/**
	 * Removes the given integration constraint from the list.
	 * 
	 * @param integrationConstraint
	 */
	void remove(IntegrationConstraint integrationConstraint);

	/**
	 * Removes all stored integration constraints from the list.
	 */
	void removeAll();

	/**
	 * Checks if the given integration constraint is stored.
	 * 
	 * @param integrationConstraint
	 * @return boolean
	 */
	boolean hasIntegrationConstraint(IntegrationConstraint integrationConstraint);
}
