/*
 * utils.graph - DiskNode.java - Copyright © 2011 David Roden
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;

import net.pterodactylus.util.graph.AbstractNode;
import net.pterodactylus.util.graph.Node;
import net.pterodactylus.util.io.Closer;
import net.pterodactylus.util.storage.Storable;
import net.pterodactylus.util.storage.StorageException;
import net.pterodactylus.util.validation.Validation;

/**
 * {@link Node} implementation that is used by {@link DiskStore}. It adds an ID
 * to the node which is used when comparing nodes using
 * {@link Object#equals(Object)}.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
class DiskNode extends AbstractNode<DiskGraph, DiskNode, DiskEdge, DiskRelationship> implements Storable {

	/** The ID of the node. */
	private final long id;

	/**
	 * Creates a new node.
	 *
	 * @param id
	 *            The ID of the node
	 * @param graph
	 *            The graph the node belongs to
	 */
	DiskNode(long id, DiskGraph graph) {
		super(graph);
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DiskNode set(String key, Object value) {
		super.set(key, value);
		getGraph().storeNode(this);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DiskNode link(DiskNode otherNode, DiskRelationship relationship) {
		Validation.begin().isNotNull("Other Node", otherNode).isNotNull("Relationship", relationship).check().isEqual("Other Node’s Graph", otherNode.getGraph(), getGraph()).check();
		getGraph().createEdge(this, otherNode, relationship);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DiskNode unlink(DiskNode otherNode, DiskRelationship relationship) {
		Validation.begin().isNotNull("Other Node", otherNode).isNotNull("Relationship", relationship).check().isEqual("Other Node’s Graph", otherNode.getGraph(), getGraph()).check();
		getGraph().removeEdge(this, otherNode, relationship);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<DiskEdge> getIncomingLinks(DiskRelationship relationship) {
		Validation.begin().isNotNull("Relationship", relationship).check();
		return getGraph().getEdgesTo(this, relationship);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<DiskEdge> getOutgoingLinks(DiskRelationship relationship) {
		Validation.begin().isNotNull("Relationship", relationship).check();
		return getGraph().getEdgesFrom(this, relationship);
	}

	//
	// OBJECT METHODS
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return (int) ((id >> 32) ^ (id & 0xffffffff));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof DiskNode)) {
			return false;
		}
		return ((DiskNode) object).id == id;
	}

	//
	// INTERFACE Storable
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getBuffer() throws StorageException {
		ByteArrayOutputStream contentStream = null;
		ObjectOutputStream objectStream = null;
		try {
			contentStream = new ByteArrayOutputStream();
			objectStream = new ObjectOutputStream(contentStream);
			objectStream.writeObject(getProperties());
		} catch (IOException ioe1) {
			throw new StorageException("Could not get bytes for DiskNode.", ioe1);
		} finally {
			Closer.close(objectStream);
			Closer.close(contentStream);
		}
		byte[] propertiesBuffer = contentStream.toByteArray();
		byte[] buffer = new byte[propertiesBuffer.length + 8];
		Storable.Utils.putLong(id, buffer, 0);
		System.arraycopy(propertiesBuffer, 0, buffer, 8, propertiesBuffer.length);
		return buffer;
	}

}
