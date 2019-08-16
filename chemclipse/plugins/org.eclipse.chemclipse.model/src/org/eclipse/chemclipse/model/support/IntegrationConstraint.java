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
 * Defines constraints on integration mode.<br/>
 * If you would like to suppress an integration with another baseline and other
 * corrections, you can add a constraint to the peak.<br/>
 * But keep in mind, it depends on the integrator to follow such constraints.
 * 
 * @author eselmeister
 */
public enum IntegrationConstraint {
	LEAVE_PEAK_AS_IT_IS; // no baseline correction, no other correction
}
