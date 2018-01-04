/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved.
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.fx.ui;

import javafx.util.Callback;

public class FXExceptionCallback implements Callback<Exception, Void> {

	@Override
	public Void call(final Exception e) {

		FXUtils.showError(e);
		return null;
	}
}
