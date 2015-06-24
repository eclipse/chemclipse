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
package org.eclipse.chemclipse.model.history;

import java.util.Date;

/**
 * This interface is used to determine the available informations that can be
 * retrieved by an editInformation object.<br/>
 * An editInformation object is used to store the edit steps of the
 * chromatogram.<br/>
 * When, e.g. some mass spectra are deleted or a filter is used on the
 * chromatogram, the information should be stored in an editInformation object.
 * 
 * @author eselmeister
 */
public interface IEditInformation {

	Date getDate();

	String getDescription();
}
