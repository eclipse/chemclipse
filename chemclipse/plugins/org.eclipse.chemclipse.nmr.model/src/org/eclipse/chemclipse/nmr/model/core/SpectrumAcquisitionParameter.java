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
package org.eclipse.chemclipse.nmr.model.core;

/**
 * <pre>                                                                                                
 *                                                                                                  
 *                                                                                                  
 *                                            [sfrq]                                                
 *                                               `                                                  
 *                                               :                                                  
 *                                               `                                                  
 *                                               :                                                  
 *      `                                        `                                         `        
 *      /                                        :                                         /        
 *      /                                        .                                         /        
 *      /```````````````````````````````````````` `````````````````````````````````````````/        
 * 
 *     x ppm                                                                               0ppm              
 *                                                                                                  
 *                                                                                                  
 *               
 *     &lt;......................................sw (hz).............................&gt;         
 *                                                                                                  
 *                                                                                                  
 * </pre>
 * 
 * @author Christoph Läubrich
 *
 */
public interface SpectrumAcquisitionParameter {

	/**
	 * [sfrq]
	 * 
	 * @return the spectrometer frequency in <b>MHz</b>, depends upon the nucleus to observe and the magnetic
	 *         field strength of the spectrometer used to acquire
	 */
	double getSpectrometerFrequency();

	/**
	 * [sw]
	 * 
	 * @return the spectral width aka 'sweep width' in <b>hz</b>
	 */
	double getSpectralWidth();
}
