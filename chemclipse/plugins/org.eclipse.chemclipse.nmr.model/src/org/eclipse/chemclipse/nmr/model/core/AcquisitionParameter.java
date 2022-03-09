/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
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
import java.math.RoundingMode;

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

	int MIN_DIGITS = 10;

	/**
	 * [sw]
	 * 
	 * @return the spectral width aka 'sweep width' in <b>hz</b>
	 */
	BigDecimal getSpectralWidth();

	/**
	 * [at]
	 * 
	 * @return the acquisition time in <b>seconds</b>
	 */
	default BigDecimal getAcquisitionTime() {

		return BigDecimal.valueOf(getNumberOfPoints()).divide(getSpectralWidth().multiply(getCarrierFrequency()), 10, RoundingMode.HALF_EVEN);
	}

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
	BigDecimal getSpectrometerFrequency();

	/**
	 * [SF01]
	 * 
	 * @return the transmitter/carrier frequency in <b>MHz</b>
	 */
	BigDecimal getCarrierFrequency();

	/**
	 * 
	 * @return the spectral offset in <b>hz</b>
	 */
	default BigDecimal getSpectralOffset() {

		BigDecimal sfo1 = getCarrierFrequency();
		BigDecimal sf = getSpectrometerFrequency();
		BigDecimal sw = getSpectralWidth();
		BigDecimal multiplicand = new BigDecimal("0.5");
		BigDecimal fac1 = sfo1.divide(sf.subtract(BigDecimal.ONE), 10, RoundingMode.HALF_EVEN);
		BigDecimal halfWidthPPM = sw.multiply(multiplicand);
		BigDecimal fac2 = sfo1.divide(sf, 10, RoundingMode.HALF_EVEN);
		// `OFFSET = (SFO1/SF-1) * 1.0e6 + 0.5 * SW * SFO1/SF
		return toHz(fac1.multiply(new BigDecimal("1E6")).add(toHz(halfWidthPPM)).multiply(fac2));
	}

	/**
	 * 
	 * @param ppm
	 *            ppm value to convert
	 * @return
	 */
	default BigDecimal toHz(BigDecimal ppm) {

		return ppm.multiply(getCarrierFrequency());
	}

	/**
	 * 
	 * @param hz
	 *            hz value to convert
	 * @return
	 */
	default BigDecimal toPPM(BigDecimal hz) {

		BigDecimal cf = getCarrierFrequency();
		return hz.divide(cf, Math.max(MIN_DIGITS, Math.max(hz.scale(), cf.scale())), RoundingMode.HALF_EVEN);
	}

	public static void print(AcquisitionParameter acquisitionParameter) {

		System.out.println("CarrierFrequency:      " + acquisitionParameter.getCarrierFrequency());
		System.out.println("NumberOfPoints:        " + acquisitionParameter.getNumberOfPoints());
		System.out.println("SpectralOffset:        " + acquisitionParameter.getSpectralOffset());
		System.out.println("SpectralWidth:         " + acquisitionParameter.getSpectralWidth());
		System.out.println("SpectrometerFrequency: " + acquisitionParameter.getSpectrometerFrequency());
		System.out.println("AcquisitionTime:       " + acquisitionParameter.getAcquisitionTime());
	}
}
