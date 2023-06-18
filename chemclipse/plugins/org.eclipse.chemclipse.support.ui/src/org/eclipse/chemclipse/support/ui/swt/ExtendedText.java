/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * This widget doesn't allow copy & paste by default.
 * It can be enabled on demand.
 */
public class ExtendedText extends Text {

	private boolean enableCopyPaste = false;

	public ExtendedText(Composite parent, int style) {

		super(parent, style);
	}

	public boolean isEnableCopyPaste() {

		return enableCopyPaste;
	}

	public void setEnableCopyPaste(boolean enableCopyPaste) {

		this.enableCopyPaste = enableCopyPaste;
	}

	@Override
	public void paste() {

		if(enableCopyPaste) {
			super.paste();
		}
	}

	@Override
	public void cut() {

		if(enableCopyPaste) {
			super.paste();
		}
	}

	@Override
	public void copy() {

		if(enableCopyPaste) {
			super.copy();
		}
	}
}