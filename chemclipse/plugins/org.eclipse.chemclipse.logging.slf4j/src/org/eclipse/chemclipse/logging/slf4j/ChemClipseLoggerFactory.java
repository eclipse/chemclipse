/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class ChemClipseLoggerFactory implements ILoggerFactory {

	private final ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<>();

	@Override
	public Logger getLogger(String name) {

		return loggers.computeIfAbsent(name, ChemClipseLoggerAdapter::new);
	}
}
