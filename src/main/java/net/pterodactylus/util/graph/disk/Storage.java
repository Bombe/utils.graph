/*
 * utils.graph - Storage.java - Copyright © 2011 David Roden
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.pterodactylus.util.graph.disk;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.pterodactylus.util.graph.StoreException;
import net.pterodactylus.util.io.Closer;

/**
 * TODO
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class Storage<T> implements Closeable {

	private static final int BLOCK_SIZE = 512;

	private final Storable.Factory<T> factory;

	private final File directory;
	private final String name;

	private final RandomAccessFile indexFile;
	private final RandomAccessFile dataFile;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	/** The directory entries in on-disk order. */
	private final List<Allocation> directoryEntries = new ArrayList<Allocation>();

	/** Maps Storables’ IDs to directory indexes. */
	private final Map<Long, Integer> idDirectoryIndexes = new HashMap<Long, Integer>();

	/** Keeps track of empty directory entries. */
	private final BitSet emptyDirectoryEntries = new BitSet();

	/** Keeps track of allocated data blocks. */
	private final BitSet allocations = new BitSet();

	public Storage(Storable.Factory<T> factory, File directory, String name) throws FileNotFoundException {
		this.factory = factory;
		this.directory = directory;
		this.name = name;
		indexFile = new RandomAccessFile(new File(directory, name + ".idx"), "rws");
		dataFile = new RandomAccessFile(new File(directory, name + ".dat"), "rws");
	}

	//
	// ACTIONS
	//

	public void open() throws IOException {
		long indexLength = indexFile.length();
		if ((indexLength % 16) != 0) {
			throw new IOException("Invalid Index Length: " + indexLength);
		}
		FileChannel indexChannel = indexFile.getChannel();
		ByteBuffer indexBuffer = indexChannel.map(MapMode.READ_ONLY, 0, indexFile.length());
		int directoryIndex = 0;
		while (indexBuffer.hasRemaining()) {
			Allocation allocation = Allocation.FACTORY.restore(indexBuffer);
			if ((allocation.getId() == 0) && (allocation.getPosition() == 0) && (allocation.getSize() == 0)) {
				emptyDirectoryEntries.set(directoryIndex);
				directoryEntries.add(null);
			} else {
				directoryEntries.add(allocation);
				idDirectoryIndexes.put(allocation.getId(), directoryIndex);
			}
			++directoryIndex;
		}
	}

	public void add(Storable storable) throws StoreException, IOException {
		ByteBuffer storableBytes = storable.getBuffer();
		int storableLength = storableBytes.remaining();
//		System.out.println("Storing " + storableLength + " bytes…");
		int blocks = getBlocks(storableLength);
//		System.out.println("Storing " + blocks + " Blocks.");
		int position = findFreeRegion(blocks);
//		System.out.println("Free Region: " + position);

		/* first, write data. */
		allocations.set(position, position + blocks);
		if (dataFile.length() < (position * BLOCK_SIZE + storableLength)) {
			dataFile.setLength(position * BLOCK_SIZE + storableLength);
		}
//		System.out.println("Storing " + name + ":" + storable.getId() + " @ "+ position + " for " + storableLength + " bytes.");
		FileChannel fileChannel = dataFile.getChannel();
		ByteBuffer buffer = fileChannel.map(MapMode.READ_WRITE, position * BLOCK_SIZE, storableLength);
		buffer.put(storableBytes);
		fileChannel.force(true);

		/* now directory entry. */
		int oldIndex = -1;
		Allocation allocation = new Allocation(storable.getId(), position, storableLength);
		int directoryIndex = emptyDirectoryEntries.nextSetBit(0);
//		System.out.println("Next Free Directory Entry: " + directoryIndex);
		if (directoryIndex == -1) {
			/* append. */
			directoryIndex = directoryEntries.size();
//			System.out.println("Appending to Directory, Entry " + directoryIndex);
			directoryEntries.add(allocation);
		} else {
			directoryEntries.set(directoryIndex, allocation);
			emptyDirectoryEntries.clear(directoryIndex);
		}
		if (idDirectoryIndexes.containsKey(storable.getId())) {
			oldIndex = idDirectoryIndexes.get(storable.getId());
//			System.out.println("Removing Old Entry at " + oldIndex + ".");
			Allocation oldAllocation = directoryEntries.set(oldIndex, null);
			emptyDirectoryEntries.set(oldIndex);
			allocations.clear(oldAllocation.getPosition(), oldAllocation.getPosition() + getBlocks(oldAllocation.getSize()));
		}
		emptyDirectoryEntries.clear(directoryIndex);
		idDirectoryIndexes.put(storable.getId(), directoryIndex);

		/* now write directory to disk. */
		fileChannel = indexFile.getChannel();
		buffer = fileChannel.map(MapMode.READ_WRITE, directoryIndex * 16, 16);
		buffer.put(allocation.getBuffer());

		/* if an old index was deleted, wipe it. */
		if (oldIndex > -1) {
			buffer = fileChannel.map(MapMode.READ_WRITE, oldIndex * 16, 16);
			buffer.put(new byte[16]);
		}
		fileChannel.force(true);
	}

	public int size() {
		return directoryEntries.size() - emptyDirectoryEntries.cardinality();
	}

	public T load(long id) throws IOException {
//		System.out.println("Loading " + name + " @ " + id  + "…");
		Integer directoryIndex = idDirectoryIndexes.get(id);
		if (directoryIndex == null) {
//			System.out.println("Not found.");
			return null;
		}
		Allocation allocation = directoryEntries.get(directoryIndex);
		FileChannel channel = dataFile.getChannel();
//		System.out.println("allocation: " + allocation);
		ByteBuffer buffer = channel.map(MapMode.READ_ONLY, allocation.getPosition() * BLOCK_SIZE, allocation.getSize());
		return factory.restore(buffer);
	}

	public int getDirectorySize() {
		return directoryEntries.size();
	}

	public Allocation getAllocation(int directoryIndex) {
		return directoryEntries.get(directoryIndex);
	}

	public void remove(Storable storable) {

	}

	public void close() {
		Closer.close(indexFile);
		Closer.close(dataFile);
	}

	//
	// PRIVATE METHODS
	//

	public int findFreeRegion(int blocks) {
		int currentBlock = -1;
		while (true) {
			int nextUsedBlock = allocations.nextSetBit(currentBlock + 1);
			if ((nextUsedBlock == -1) || ((nextUsedBlock - currentBlock - 1) >= blocks)) {
				return currentBlock + 1;
			}
			currentBlock = nextUsedBlock;
		}
	}

	//
	// STATIC METHODS
	//

	static int getBlocks(long size) {
		if (size == 0) {
			return 1;
		}
		return (int) ((size - 1) / BLOCK_SIZE + 1);
	}

}