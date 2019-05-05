package com.github.f4b6a3.uuid.state;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
		try {

			Properties properties = new Properties();
			properties.setProperty(PROPERTY_TIMESTAMP, ByteUtil.toHexadecimal(timestamp));
			properties.setProperty(PROPERTY_CLOCKSEQ, ByteUtil.toHexadecimal(clockSequence));
			properties.setProperty(PROPERTY_NODEID, ByteUtil.toHexadecimal(nodeIdentifier));
			properties.store(new FileWriter(this.fileName), null);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load() {
		try {

			Properties properties = new Properties();
			properties.load(new FileInputStream(fileName));
			String timestamp = properties.getProperty(PROPERTY_TIMESTAMP, "0");
			String clockseq = properties.getProperty(PROPERTY_CLOCKSEQ, "0");
			String nodeid = properties.getProperty(PROPERTY_NODEID, "0");

			this.timestamp = ByteUtil.toNumber(timestamp);
			this.clockSequence = ((int) ByteUtil.toNumber(clockseq)) & 0x00003FFF;
			this.nodeIdentifier = ByteUtil.toNumber(nodeid) & 0x0000FFFFFFFFFFFFL;

		} catch (FileNotFoundException e) {
			// do nothing
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
