package com.pivotal.pxf.plugins.hdfs.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.io.compress.Decompressor;

/**
 * Codec class for UtilitiesTest
 * Can't be embedded inside UtilitiesTest due to junit limitation.
 */
public class NotSoNiceCodec implements CompressionCodec {

	@Override
	public CompressionOutputStream createOutputStream(OutputStream out)
			throws IOException {
		return null;
	}

	@Override
	public CompressionOutputStream createOutputStream(OutputStream out,
			Compressor compressor) throws IOException {
		return null;
	}

	@Override
	public Class<? extends Compressor> getCompressorType() {
		return null;
	}

	@Override
	public Compressor createCompressor() {
		return null;
	}

	@Override
	public CompressionInputStream createInputStream(InputStream in)
			throws IOException {
		return null;
	}

	@Override
	public CompressionInputStream createInputStream(InputStream in,
			Decompressor decompressor) throws IOException {
		return null;
	}

	@Override
	public Class<? extends Decompressor> getDecompressorType() {
		return null;
	}

	@Override
	public Decompressor createDecompressor() {
		return null;
	}

	@Override
	public String getDefaultExtension() {
		return null;
	}

}