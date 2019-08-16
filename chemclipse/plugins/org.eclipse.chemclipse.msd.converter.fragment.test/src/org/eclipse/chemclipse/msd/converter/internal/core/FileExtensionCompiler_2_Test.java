/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.internal.core;

import org.eclipse.chemclipse.converter.support.FileExtensionCompiler;

import junit.framework.TestCase;

public class FileExtensionCompiler_2_Test extends TestCase {

	private FileExtensionCompiler fileExtensionCompiler;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testFileExtensionCompiler_1() {

		fileExtensionCompiler = new FileExtensionCompiler("", true);
		assertEquals("*", fileExtensionCompiler.getCompiledFileExtension());
	}

	public void testFileExtensionCompiler_2() {

		fileExtensionCompiler = new FileExtensionCompiler("", false);
		assertEquals("*", fileExtensionCompiler.getCompiledFileExtension());
	}

	public void testFileExtensionCompiler_3() {

		fileExtensionCompiler = new FileExtensionCompiler(null, true);
		assertEquals("*", fileExtensionCompiler.getCompiledFileExtension());
	}

	public void testFileExtensionCompiler_4() {

		fileExtensionCompiler = new FileExtensionCompiler(null, false);
		assertEquals("*", fileExtensionCompiler.getCompiledFileExtension());
	}
}
