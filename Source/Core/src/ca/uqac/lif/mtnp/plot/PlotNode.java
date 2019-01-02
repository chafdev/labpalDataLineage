/*
  MTNP: Manipulate Tables N'Plots
  Copyright (C) 2017 Sylvain Hallé

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.mtnp.plot;

import java.util.regex.Pattern;

import ca.uqac.lif.mtnp.table.TableFunctionNode;
import ca.uqac.lif.petitpoucet.NodeFunction;
import ca.uqac.lif.petitpoucet.OwnershipManager;

/**
 * Provenance node that links to a whole plot
 * @author Sylvain Hallé
 */
public class PlotNode implements NodeFunction 
{
	/**
	 * The plot this node represents
	 */
	protected final Plot m_plot;

	public PlotNode(Plot p)
	{
		super();
		m_plot = p;
	}

	public static String getDataPointId(Plot p)
	{
		// We add a ".0" suffix so that a PDF viewer does not take the
		// hyperlink for a filename
		return "P" + p.getId() + ".0";
	}

	@Override
	public String getDataPointId() 
	{
		return getDataPointId(m_plot);
	}

	@Override
	public NodeFunction dependsOn() 
	{
		// The plot depends on a whole table
		return new TableFunctionNode(m_plot.getTable(), 0, 0);
	}

	public Plot getOwner()
	{
		return m_plot;
	}

	/**
	 * Gets the owner of a datapoint
	 * @param manager The manager responsible of ownership relationships
	 * @param datapoint_id The ID of the data point to fetch
	 * @return The owner, or {@code null} if this object could not
	 * find the owner
	 */
	public static Plot getOwner(OwnershipManager manager, String datapoint_id)
	{
		if (!datapoint_id.startsWith("P"))
			return null;
		String[] parts = datapoint_id.split(Pattern.quote(NodeFunction.s_separator));
		return (Plot) manager.getObjectWithId(parts[0]);
	}

	public static NodeFunction dependsOn(Plot p, String datapoint_id)
	{
		// Parse the datapoint ID and call the experiment on the extracted values
		if (!datapoint_id.startsWith("P"))
			return null;
		String[] parts = datapoint_id.split(Pattern.quote(NodeFunction.s_separator));
		int id = Integer.parseInt(parts[0].substring(1).trim());
		if (id != p.getId())
		{
			// Wrong experiment
			return null;
		}
		return new PlotNode(p);
	}

}
