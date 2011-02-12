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
import java.nio.ByteBuffer;
import java.util.Set;

import net.pterodactylus.util.graph.AbstractNode;
import net.pterodactylus.util.graph.Node;
import net.pterodactylus.util.graph.StoreException;
import net.pterodactylus.util.io.Closer;
import net.pterodactylus.util.validation.Validation;

/**
 * TODO
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class DiskNode extends AbstractNode<DiskGraph, DiskNode, DiskEdge, DiskRelationship> implements Storable {

	private final long id;

	DiskNode(long id, DiskGraph graph) {
		super(graph);
		this.id = id;
	}

	public long getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node set(String key, Object value) {
		super.set(key, value);
		getGraph().storeNode(this);
		return this;
	}

	@Override
	public Node link(DiskNode otherNode, DiskRelationship relationship) {
		Validation.begin().isNotNull("Other Node", otherNode).isNotNull("Relationship", relationship).check().isEqual("Other Node’s Graph", otherNode.getGraph(), getGraph()).check();
		getGraph().createEdge(this, otherNode, relationship);
		return this;
	}

	@Override
	public Node unlink(DiskNode otherNode, DiskRelationship relationship) {
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
	public ByteBuffer getBuffer() throws StoreException {
		ByteArrayOutputStream contentStream = null;
		ObjectOutputStream objectStream = null;
		try {
			contentStream = new ByteArrayOutputStream();
			objectStream = new ObjectOutputStream(contentStream);
			objectStream.writeObject(getProperties());
		} catch (IOException ioe1) {
			throw new StoreException("Could not get bytes for DiskNode.", ioe1);
		} finally {
			Closer.close(objectStream);
			Closer.close(contentStream);
		}
		byte[] propertiesBuffer = contentStream.toByteArray();
		ByteBuffer buffer = ByteBuffer.allocate(8 + propertiesBuffer.length);
		buffer.putLong(id);
		buffer.put(propertiesBuffer);
		buffer.flip();
		return buffer;
	}

}
