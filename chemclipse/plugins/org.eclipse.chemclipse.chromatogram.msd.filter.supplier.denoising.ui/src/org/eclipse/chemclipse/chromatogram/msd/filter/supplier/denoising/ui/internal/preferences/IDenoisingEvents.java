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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.internal.preferences;

import org.eclipse.e4.core.services.events.IEventBroker;

public interface IDenoisingEvents {

	String PROPERTY_NOISE_MASS_SPECTRA = IEventBroker.DATA; // Noise Mass Spectra
	String TOPIC_NOISE_MASS_SPECTRA_UPDATE = "filter/msd/update/supplier/denoising/noisemassspectra";
}
