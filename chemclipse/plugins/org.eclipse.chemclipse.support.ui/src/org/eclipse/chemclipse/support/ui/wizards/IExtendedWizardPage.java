/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.wizards;

import java.text.DecimalFormat;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Text;

public interface IExtendedWizardPage extends IWizardPage {

	boolean canFinish();

	void setDefaultValues();

	String validateInput(Text textInput, String errorMessage);

	String validateDoubleInput(Text textInput, DecimalFormat decimalFormat, String errorMessage);

	String validateDoubleInput(Text textInput, DecimalFormat decimalFormat, double min, double max, String errorMessage);

	String validateInput(Combo comboInput, String errorMessage);

	String validateDateInput(DateTime dateTime, String errorMessage);
}
