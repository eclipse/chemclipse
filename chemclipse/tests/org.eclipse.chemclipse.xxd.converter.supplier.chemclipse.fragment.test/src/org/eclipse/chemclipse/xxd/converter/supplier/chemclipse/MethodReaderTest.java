/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.methods.MethodExportConverter;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.methods.MethodImportConverter;
import org.junit.Ignore;
import org.junit.Test;

@Ignore // TODO
public class MethodReaderTest {

	private static final String FIRST_STREAM_SUPPORT_FORMAT = "Format_v0.0.3.ocm";
	private static final String[] FORMAT_FILES = {"Format_v0.0.1.ocm", "Format_v0.0.2.ocm", FIRST_STREAM_SUPPORT_FORMAT};
	MethodImportConverter converter = new MethodImportConverter();
	MethodExportConverter exportConverter = new MethodExportConverter();

	@Test
	public void testRead() throws IOException, URISyntaxException {

		boolean stream = false;
		for(String filename : FORMAT_FILES) {
			File file = getFile(filename);
			if(FIRST_STREAM_SUPPORT_FORMAT.equals(filename)) {
				stream = true;
			}
			IProcessMethod result1 = checkRead(file, false);
			if(stream) {
				assertTrue("result read from file differs from stream!", result1.contentEquals(checkRead(file, true), true));
			}
		}
	}

	private File getFile(String filename) throws URISyntaxException {

		URL url = MethodReaderTest.class.getResource("/files/methods/" + filename);
		File file = new File(url.toURI());
		return file;
	}

	private IProcessMethod checkRead(File file, boolean withStream) {

		try {
			IProcessingInfo<IProcessMethod> result = withStream ? converter.readFrom(new FileInputStream(file), file.getName(), null) : converter.convert(file, null);
			checkResult(file.getAbsolutePath(), result, withStream ? "stream api" : "file api");
			return result.getProcessingResult();
		} catch(IOException e) {
			throw new AssertionError(e);
		}
	}

	private void checkResult(String filename, IProcessingInfo<?> convert, String context) {

		assertNotNull("[" + context + "] IProcessingInfo was null for " + filename, convert);
		assertFalse("[" + context + "] has errors (" + convert.getMessages() + ")", convert.hasErrorMessages());
		assertNotNull("[" + context + "] IProcessingInfo#getProcessingResult() for " + filename + " was null", convert.getProcessingResult());
	}

	@Test
	public void testReadWriteLatest() throws IOException {

		File tempFile = File.createTempFile("test", ".ocm");
		tempFile.deleteOnExit();
		ProcessingInfo<Object> messages = new ProcessingInfo<>(new Object());
		ProcessMethod method = new ProcessMethod(ProcessMethod.CHROMATOGRAPHY);
		method.setSourceFile(tempFile);
		method.setOperator("Test-Operator");
		method.getMetaData().put("Meta", "Value");
		method.addProcessEntry(createEntry(method, "main1"));
		method.addProcessEntry(createEntryWithChilds(method, "main.withchilds"));
		method.addProcessEntry(createEntry(method, "main1"));
		exportConverter.convert(tempFile, method, messages, null);
		ProcessMethod read = (ProcessMethod)checkRead(tempFile, false);
		assertTrue("result read from file differs from stream!", read.contentEquals(checkRead(tempFile, true), true));
		assertNotEquals("not different objects!", System.identityHashCode(method), System.identityHashCode(read));
		assertEquals(method.getUUID(), read.getUUID());
		assertEquals(method.getName(), read.getName());
		assertEquals(method.getOperator(), read.getOperator());
		assertEquals("number of entries mismatched", method.getNumberOfEntries(), read.getNumberOfEntries());
		ProcessEntry subEntry = (ProcessEntry)read.getEntries().get(1);
		assertEquals(subEntry.getEntries().size(), 2);
	}

	@Test
	public void testNestedMethodRead() throws URISyntaxException, IOException {

		IProcessingInfo<IProcessMethod> convert = converter.convert(getFile("ChromIdentMethod.ocm"), null);
		checkResult("ChromIdentMethod.ocm", convert, "nested");
		IProcessMethod result = convert.getProcessingResult();
		assertEquals(3, result.getNumberOfEntries());
		ProcessEntryContainer next = (ProcessEntryContainer)result.iterator().next();
		assertEquals(1, next.getNumberOfEntries());
	}

	private ProcessEntry createEntryWithChilds(ProcessMethod method, String id) {

		ProcessEntry createEntry = createEntry(method, id);
		createEntry.addProcessEntry(createEntry(createEntry, "child1"));
		createEntry.addProcessEntry(createEntry(createEntry, "child2"));
		return createEntry;
	}

	private ProcessEntry createEntry(ProcessEntryContainer method, String id) {

		ProcessEntry entry = new ProcessEntry(method);
		entry.setProcessorId(id);
		return entry;
	}
}
