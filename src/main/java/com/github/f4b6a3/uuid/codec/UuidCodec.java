package com.github.f4b6a3.uuid.codec;

import java.util.UUID;

public interface UuidCodec<T> {
	public T encode(UUID uuid);

	public UUID decode(T item);
}