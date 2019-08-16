/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.preferences;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * @author eselmeister
 */
public class IonInputValidator implements IInputValidator {

	@Override
	public String isValid(String newText) {

		/*
		 * Test if the input is an integer value.<br/> If yes, return null,
		 * otherwise throw a failure description.
		 */
		try {
			String.valueOf(Integer.parseInt(newText));
		} catch(NumberFormatException e) {
			return "The input must be an integer value.";
		}
		return null;
	}
}
