/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.components.peaks;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public interface IIncludeBackground {

	/**
	 * Return whether the peak will show the background or not.
	 * 
	 * @return boolean
	 */
	boolean isIncludeBackground();

	/**
	 * If true, the peak will show the background additionally.
	 * 
	 * @param includeBackground
	 */
	void setIncludeBackground(boolean includeBackground);
}
