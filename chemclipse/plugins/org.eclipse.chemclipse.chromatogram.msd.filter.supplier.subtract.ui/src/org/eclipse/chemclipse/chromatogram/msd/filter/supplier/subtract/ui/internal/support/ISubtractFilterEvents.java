/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.internal.support;

import org.eclipse.e4.core.services.events.IEventBroker;

public interface ISubtractFilterEvents {

	String PROPERTY_UPDATE = IEventBroker.DATA; // Always true
	String TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM = "filter/supplier/subtract/update/session/subtractmassspectrum";
}
