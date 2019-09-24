/*
 * MIT License
 * 
 * Copyright (c) 2018-2019 Fabio Lima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.f4b6a3.uuid.state;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.SettingsUtil;

public class FileUuidState extends AbstractUuidState {

	private static final String FILE_NAME = "uuidcreator";
	private static final String FILE_EXTENSION = "state";

	private static final String PROPERTY_TIMESTAMP = "timestamp";
	private static final String PROPERTY_CLOCKSEQ = "clockseq";
	private static final String PROPERTY_NODEID = "nodeid";

	private String directory;
	private String fileName;

	private boolean valid;

	public FileUuidState() {
		super();
		this.directory = SettingsUtil.getStateDirectory();
		this.updateFileName();
		this.load();
	}

	private void updateFileName() {
		this.fileName = String.join("/", directory, FILE_NAME);
		this.fileName = String.join(".", this.fileName, FILE_EXTENSION);
	}

	@Override
	public void store() {
		try (FileWriter file = new FileWriter(this.fileName)) {
			String timestampHex = ByteUtil.toHexadecimal(this.timestamp);
			String clockseqHex = ByteUtil.toHexadecimal(this.clockSequence);
			String nodeidHex = ByteUtil.toHexadecimal(this.nodeIdentifier);
			this.validate(timestampHex, clockseqHex, nodeidHex);

			if (this.valid) {
				Properties properties = new Properties();
				properties.setProperty(PROPERTY_TIMESTAMP, timestampHex);
				properties.setProperty(PROPERTY_CLOCKSEQ, clockseqHex);
				properties.setProperty(PROPERTY_NODEID, nodeidHex);
				properties.store(file, null);
			}
		} catch (IOException e) {
			// Do nothing
		}
	}

	@Override
	public void load() {
		try (FileReader file = new FileReader(fileName)) {

			Properties properties = new Properties();
			properties.load(file);
			String timestampHex = properties.getProperty(PROPERTY_TIMESTAMP, "0");
			String clockseqHex = properties.getProperty(PROPERTY_CLOCKSEQ, "0");
			String nodeidHex = properties.getProperty(PROPERTY_NODEID, "0");
			this.validate(timestampHex, clockseqHex, nodeidHex);

			if (this.valid) {
				this.timestamp = ByteUtil.toNumber(timestampHex);
				this.clockSequence = ((int) ByteUtil.toNumber(clockseqHex)) & 0x00003FFF;
				this.nodeIdentifier = ByteUtil.toNumber(nodeidHex) & 0x0000FFFFFFFFFFFFL;
			}
		} catch (IOException e) {
			// do nothing
		}
	}

	@Override
	public boolean isValid() {
		return this.valid;
	}

	private void validate(String timestampHex, String clockseqHex, String nodeidHex) {

		String zero = "^[0]{1,16}$";
		String hexa = "[0-9a-fA-F]{1,16}";

		boolean validTimestamp = timestampHex != null && timestampHex.matches(hexa) && !timestampHex.matches(zero);
		boolean validClockseq = clockseqHex != null && clockseqHex.matches(hexa) && !clockseqHex.matches(zero);
		boolean validNodeid = nodeidHex != null && nodeidHex.matches(hexa) && !nodeidHex.matches(zero);

		this.valid = validTimestamp && validClockseq && validNodeid;
	}
}
