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
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class ObjectStreamMethodFormat extends GenericStreamMethodFormat {

	protected ObjectStreamMethodFormat(String version) {
		super(version);
	}

	@Override
	protected final IProcessMethod deserialize(InputStream stream, IMessageConsumer consumer, IProgressMonitor monitor) throws IOException {

		try {
			return readObjectFromStream(new ObjectInputStream(stream), consumer, monitor);
		} catch(ClassNotFoundException e) {
			throw new IOException("corrupted stream", e);
		}
	}

	@Override
	protected final void serialize(OutputStream stream, IProcessMethod processMethod, IMessageConsumer consumer, IProgressMonitor monitor) throws IOException {

		ObjectOutputStream objectStream = new ObjectOutputStream(stream);
		writeObjectToStream(objectStream, processMethod, consumer, monitor);
		objectStream.flush();
	}

	protected abstract void writeObjectToStream(ObjectOutputStream stream, IProcessMethod processMethod, IMessageConsumer consumer, IProgressMonitor monitor) throws IOException;

	protected abstract IProcessMethod readObjectFromStream(ObjectInputStream stream, IMessageConsumer consumer, IProgressMonitor monitor) throws IOException, ClassNotFoundException;

	public static interface ObjectOutputStreamSerializer<T> {

		void serialize(T item, ObjectOutputStream stream) throws IOException;
	}

	public static interface ObjectInputStreamDeserializer<T> {

		T deserialize(ObjectInputStream stream) throws IOException, ClassNotFoundException;
	}

	public static <T> void readItems(ObjectInputStream stream, ObjectInputStreamDeserializer<T> deserializer, Consumer<? super T> consumer) throws IOException, ClassNotFoundException {

		int count = stream.readInt();
		for(int i = 0; i < count; i++) {
			consumer.accept(deserializer.deserialize(stream));
		}
	}

	public static <T> void writeIterable(Iterable<T> iterable, ObjectOutputStream stream, ObjectOutputStreamSerializer<? super T> serializer) throws IOException {

		if(iterable == null) {
			stream.writeInt(0);
		} else {
			ArrayList<T> list = new ArrayList<>();
			iterable.forEach(list::add);
			stream.writeInt(list.size());
			for(T item : list) {
				serializer.serialize(item, stream);
			}
		}
	}

	public static <T> void writeArray(T[] array, ObjectOutputStream stream, ObjectOutputStreamSerializer<? super T> serializer) throws IOException {

		stream.writeInt(array.length);
		for(T item : array) {
			serializer.serialize(item, stream);
		}
	}

	public static <Key, Value> void writeMap(Map<Key, Value> map, ObjectOutputStream stream, ObjectOutputStreamSerializer<? super Key> keySerializer, ObjectOutputStreamSerializer<? super Value> valueSerializer) throws IOException {

		Set<Entry<Key, Value>> entrySet = map.entrySet();
		stream.writeInt(entrySet.size());
		for(Entry<Key, Value> entry : entrySet) {
			keySerializer.serialize(entry.getKey(), stream);
			valueSerializer.serialize(entry.getValue(), stream);
		}
	}

	public static <Key, Value> void readMap(ObjectInputStream stream, ObjectInputStreamDeserializer<? extends Key> keyDeserialization, ObjectInputStreamDeserializer<? extends Value> valueDeserialization, BiConsumer<? super Key, ? super Value> consumer) throws IOException, ClassNotFoundException {

		int count = stream.readInt();
		for(int i = 0; i < count; i++) {
			Key key = keyDeserialization.deserialize(stream);
			Value value = valueDeserialization.deserialize(stream);
			consumer.accept(key, value);
		}
	}

	public static <T extends Enum<T>> ObjectInputStreamDeserializer<T> enumDeserialization(Class<T> type, T missingValue) {

		T[] constants = type.getEnumConstants();
		return stream -> {
			Object readObject = stream.readObject();
			for(T constant : constants) {
				if(constant.name().equals(readObject)) {
					return constant;
				}
			}
			return missingValue;
		};
	}

	public static <T extends Enum<T>> void writeEnum(T enumValue, ObjectOutputStream stream) throws IOException {

		stream.writeObject(enumValue.name());
	}

	public static String readString(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		return (String)stream.readObject();
	}

	public static void writeString(String str, ObjectOutputStream stream) throws IOException {

		stream.writeObject(str);
	}
}
