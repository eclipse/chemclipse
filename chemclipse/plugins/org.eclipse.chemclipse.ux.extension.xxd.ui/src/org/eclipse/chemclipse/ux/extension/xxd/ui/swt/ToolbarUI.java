/*******************************************************************************
 * Copyright (c) 2018 Christoph Läubrich.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

/**
 * Interface an UI can implement to state that it has a toolbar that can be switched on/off
 * 
 * @author Christoph Läubrich
 *
 */
public interface ToolbarUI {

	void setToolbarVisible(boolean visible);
}
