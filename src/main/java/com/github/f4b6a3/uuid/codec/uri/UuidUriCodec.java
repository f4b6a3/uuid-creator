package com.github.f4b6a3.uuid.codec.uri;

import java.net.URI;
import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.UuidStringCodec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

public class UuidUriCodec implements UuidCodec<java.net.URI> {

	protected static final String URN_PREFIX = "urn:uuid:";

	@Override
	public URI encode(UUID uuid) {
		return URI.create(URN_PREFIX + LazyHolder.CODEC.encode(uuid));
	}

	@Override
	public UUID decode(URI uri) {
		if (uri == null || !uri.toString().startsWith(URN_PREFIX)) {
			throw new InvalidUuidException("Invalid URI: \"" + uri + "\"");
		}
		return LazyHolder.CODEC.decode(uri.toString());
	}

	private static class LazyHolder {
		private static final UuidCodec<String> CODEC = new UuidStringCodec();
	}
}
