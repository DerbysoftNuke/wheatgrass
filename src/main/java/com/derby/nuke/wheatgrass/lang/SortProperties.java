package com.derby.nuke.wheatgrass.lang;

import java.io.*;
import java.util.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SortProperties extends Properties {

	private static final long serialVersionUID = 1L;
	private Comparator<String> comparator;

	public SortProperties() {
		super();
	}

	public SortProperties(Map<? extends Object, ? extends Object> properties) {
		super();
		putAll(properties);
	}

	public SortProperties(Properties properties) {
		super();
		putAll(properties);
	}

	@Override
	public Enumeration<Object> keys() {
		Vector keyVector = new Vector();
		for (Object key : super.keySet()) {
			keyVector.add(key);
		}
		if (comparator == null) {
			Collections.sort(keyVector);
		} else {
			Collections.sort(keyVector, comparator);
		}
		return keyVector.elements();
	}

	public void setComparator(Comparator<String> comparator) {
		this.comparator = comparator;
	}

	@Override
	public void store(Writer writer, String comments) throws IOException {
		store(writer, false);
	}

	public String toString() {
		StringWriter writer = new StringWriter();
		try {
			store(writer, false);
		} catch (IOException ignored) {

		}
		return writer.toString();
	}

	private void store(Writer writer, boolean escUnicode) throws IOException {
		BufferedWriter bw = new BufferedWriter(new BufferedWriter(writer));
		synchronized (this) {
			for (Enumeration e = keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String val = (String) get(key);
				key = saveConvert(key, true, escUnicode);
				val = saveConvert(val, false, escUnicode);
				bw.write(key + "=" + val);
				bw.newLine();
			}
		}
		bw.flush();
	}

	@Override
	public void store(OutputStream out, String comments) throws IOException {
		store(new OutputStreamWriter(out, "UTF-8"), true);
	}

	private String saveConvert(String theString, boolean escapeSpace, boolean escapeUnicode) {
		int len = theString.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuilder outBuffer = new StringBuilder(bufLen);

		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			// Handle common case first, selecting largest block that
			// avoids the specials below
			if ((aChar > 61) && (aChar < 127)) {
				if (aChar == '\\') {
					outBuffer.append('\\');
					outBuffer.append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch (aChar) {
			case ' ':
				if (x == 0 || escapeSpace)
					outBuffer.append('\\');
				outBuffer.append(' ');
				break;
			case '\t':
				outBuffer.append('\\');
				outBuffer.append('t');
				break;
			case '\n':
				outBuffer.append('\\');
				outBuffer.append('n');
				break;
			case '\r':
				outBuffer.append('\\');
				outBuffer.append('r');
				break;
			case '\f':
				outBuffer.append('\\');
				outBuffer.append('f');
				break;
			case '=': // Fall through
			case ':': // Fall through
			case '#': // Fall through
			case '!':
				outBuffer.append('\\');
				outBuffer.append(aChar);
				break;
			default:
				if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
					outBuffer.append('\\');
					outBuffer.append('u');
					outBuffer.append(toHex((aChar >> 12) & 0xF));
					outBuffer.append(toHex((aChar >> 8) & 0xF));
					outBuffer.append(toHex((aChar >> 4) & 0xF));
					outBuffer.append(toHex(aChar & 0xF));
				} else {
					outBuffer.append(aChar);
				}
			}
		}
		return outBuffer.toString();
	}

	private static char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
	}

	private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

}
