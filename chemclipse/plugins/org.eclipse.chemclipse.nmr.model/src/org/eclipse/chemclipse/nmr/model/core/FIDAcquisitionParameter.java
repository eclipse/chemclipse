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
 * 
 * @author Christoph Läubrich
 *
 */
public interface FIDAcquisitionParameter {

	/**
	 * [pw]
	 * 
	 * @return The pulse width is in <b>microseconds</b>
	 */
	double getPulseWidth();

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
	 * 
	 * @return the time before starting the pulse in <b>seconds</b>
	 */
	double getRecycleDelay();
}
