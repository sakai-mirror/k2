/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.sakaiproject.kernel.jcr.preload;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Dumper
{

	private static final Log LOG = LogFactory.getLog(Dumper.class);
        private static final boolean debug = LOG.isDebugEnabled();

	/** Recursively outputs the contents of the given node. */
	public static void dump(Node node)
	{
		if (!debug)
		{
			return;
		}
		// First output the node path
		try
		{
			// Skip the virtual (and large!) jcr:system subtree
			if (node.getName().equals("jcr:system"))
			{
				return;
			}
			if (node.getName().equals("jcr:data"))
			{
				return;
			}

			// Then output the properties
			StringBuffer sb = new StringBuffer();
			PropertyIterator properties = node.getProperties();
			while (properties.hasNext())
			{
				Property property = properties.nextProperty();
				if (property.getDefinition().isMultiple())
				{
					// A multi-valued property, print all values
					Value[] values = property.getValues();
					for (int i = 0; i < values.length; i++)
					{

						String value = values[i].getString();
						if (value.length() < 1024)
						{
							sb.append(property.getPath()).append(" = ").append(value)
									.append("\n");
						}
						else
						{
							sb.append(property.getPath()).append(" = Value Length ")
									.append(value.length()).append("\n");
						}
					}
				}
				else
				{
					// A single-valued property
					String value = property.getString();
					if (value.length() < 1024)
					{
						sb.append(property.getPath()).append(" = ").append(value).append(
								"\n");
					}
					else
					{
						sb.append(property.getPath()).append(" = Value Length ").append(
								value.length()).append("\n");
					}
				}
			}

			LOG.debug("Node " + node.getPath() + "\n" + sb.toString());
			// Finally output all the child nodes recursively
			NodeIterator nodes = node.getNodes();
			while (nodes.hasNext())
			{
				dump(nodes.nextNode());
			}
		}
		catch (RepositoryException rex)
		{
			rex.printStackTrace();
		}
	}
}
