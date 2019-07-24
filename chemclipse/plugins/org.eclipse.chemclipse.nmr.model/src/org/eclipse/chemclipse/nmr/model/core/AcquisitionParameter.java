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

import java.math.BigDecimal;

/**
 * 
 * Defines the AcquisitionParameters used in the FID/Time domain
 * 
 * <pre>                                                     Pulse Width
 *                                                  .```````````````````````````````:                                                                            
 *                                                  .                               /                                                                            
 *                                                  .                               o`                                                                           
 *                                                  .                               o-                                                                           
 *                                                  .                               o/                                                                           
 *                                                  .                               ::                                                                           
 *                                                  .                               :::      Aquisition Time                                                     
 *                                                  .                               :/o                                                                          
 *                                                  .                               :/o-                                                                         
 *                                                  .                               :/os                                                                         
 *                             Recycle delay        .                               :/oo::                                                                       
 *                                                  .                               :/o/+/o.`                                                                    
 *      ````````````````````````````````````````````.                               ./o+o/+++//--..`..```````````````                                            
 *                                                                                  /o/o//:.                                                                    
 *                                                                                  /o/o:                                                                       
 *                                                                                  o/:o                                                                        
 *                                                                                  s-/`                                                                        
 *                                                                                  s+/                                                                         
 *                                                                                  sy-.                                                                        
 *                                                                                  s:                                                                          
 *                                                                                  s                                                                           
 *                                                                                  s                                                                           
 *                                                                                  s                                                                           
 *                                                                                  `            
 *                                                                                  ................................
 *                                                                                           Number if points
 * </pre>
 * <pre>                                                                                                
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
 * </pre>
 * 
 * @author Christoph Läubrich
 *
 */
public interface AcquisitionParameter {

	/**
	 * [sw]
	 * 
	 * @return the spectral width aka 'sweep width' in <b>hz</b>
	 */
	double getSpectralWidth();

	/**
	 * [at]
	 * 
	 * @return the acquisition time in <b>seconds</b>
	 */
	BigDecimal getAcquisitionTime();

	/**
	 * 
	 * @return the number of discrete data points at acquisition time
	 */
	int getNumberOfPoints();

	/**
	 * [SF]
	 * 
	 * @return the spectrometer frequency in <b>MHz</b>
	 */
	double getSpectrometerFrequency();

	/**
	 * [SF01]
	 * 
	 * @return the transmitter/carrier frequency in <b>MHz</b>
	 */
	double getCarrierFrequency();

	/**
	 * 
	 * @return the spectral offset in <b>hz</b>
	 */
	default double getSpectralOffset() {

		return ppmToHz(getCarrierFrequency() / (getSpectrometerFrequency() - 1) * 1E6 + 0.5 * hzToPpm(getSpectralWidth()) + (getCarrierFrequency() / getSpectrometerFrequency()));
	}

	/**
	 * 
	 * @param ppm
	 *            ppm value to convert
	 * @return
	 */
	default double ppmToHz(double ppm) {

		return ppm * getCarrierFrequency();
	}

	/**
	 * 
	 * @param hz
	 *            hz value to convert
	 * @return
	 */
	default double hzToPpm(double hz) {

		return getCarrierFrequency() / hz;
	}
}
