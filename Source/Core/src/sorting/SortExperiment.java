/*
  LabPal, a versatile environment for running experiments on a computer
  Copyright (C) 2015-2017 Sylvain Hallé

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
package sorting;

import java.io.File;
import java.util.Random;

import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.labpal.FileHelper;

public abstract class SortExperiment extends Experiment
{
	/**
	 * The folder where the generated data will be put
	 */
	protected static final transient String s_dataDir = "data/";


	SortExperiment()
	{
		super();
		describe("name", "Name of the sorting algorithm");
		describe("size", "Size of the array to sort");
		describe("time", "Sorting time (in ms)");
		setEditableParameters("size");
	}

	public SortExperiment(String name, int size)
	{
		this();
		setInput("name", name);
		setInput("size", size);
		setDescription("Sorts an array of size " + size + " with " + name);
	}

	@Override
	public final boolean prerequisitesFulfilled()
	{
		return FileHelper.fileExists(getDataFilename());
	}

	@Override
	public final void cleanPrerequisites()
	{
		String filename = getDataFilename();
		if (FileHelper.fileExists(filename))
		{
			FileHelper.deleteFile(filename);
		}
	}

	@Override
	public final void fulfillPrerequisites()
	{
		// Generates a random list of integers of given size, and saves it
		// to a file
		Random random = getRandom();
		String filename = getDataFilename();
		StringBuilder out = new StringBuilder();
		int size = readInt("size");
		int range = 2 * size;
		for (int i = 0; i < size; i++)
		{
			int number = random.nextInt(range);
			if (i > 0)
			{
				out.append(",");
			}
			out.append(number);
		}
		FileHelper.writeFromString(new File(filename), out.toString());
	}

	/**
	 * Generates the data filename based on the experiment's parameters
	 * @param input The experiment's input parameters
	 * @return
	 */
	protected final String getDataFilename()
	{
		int size = readInt("size");
		return s_dataDir + "list-" + size + ".txt";
	}

	@Override
	public void execute() throws ExperimentException
	{
		int[] array = getArray();
		if (array == null)
		{
			throw new ExperimentException("A null array was passed to this experiment");
		}
		long start_time = System.nanoTime();
		sort(array);
		long end_time = System.nanoTime();
		write("time", (end_time - start_time) / 1000000f);
	}

	protected final int[] getArray()
	{
		int size = readInt("size");
		int[] array = new int[size];
		String filename = getDataFilename();
		String contents = FileHelper.readToString(new File(filename));
		String[] str = contents.split(",");
		for (int i = 0; i < size; i++)
		{
			array[i] = Integer.parseInt(str[i].trim());
		}
		return array;
	}

	protected abstract void sort(int[] array);

	@Override
	public float getDurationEstimate(float factor)
	{
		float size = readFloat("size");
		return (size / 20000) / factor;
	}

	@Override
	public String toString()
	{
		String out = "";
		out += readString("name");
		out += " " + (readInt("size") / 1000) + "k";
		return out;
	}
	
	@Override
	public String getDescription()
	{
		return "Sorts an array of size " + readInt("size") + " using " + readString("name");
	}

}
