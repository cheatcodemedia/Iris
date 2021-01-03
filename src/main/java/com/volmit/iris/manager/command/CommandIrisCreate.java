package com.volmit.iris.manager.command;

import com.volmit.iris.Iris;
import com.volmit.iris.manager.IrisDataManager;
import com.volmit.iris.nms.INMS;
import com.volmit.iris.object.IrisDimension;
import com.volmit.iris.pregen.Pregenerator;
import com.volmit.iris.scaffold.IrisWorldCreator;
import com.volmit.iris.scaffold.engine.IrisAccess;
import com.volmit.iris.util.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;

public class CommandIrisCreate extends MortarCommand
{
	public CommandIrisCreate()
	{
		super("create", "new", "+");
		requiresPermission(Iris.perm.studio);
		setCategory("Create");
		setDescription("Create a new Iris World!");
	}

	@Override
	public void addTabOptions(MortarSender sender, String[] args, KList<String> list) {

	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(args.length < 1)
		{
			sender.sendMessage("/iris create <NAME> [type=overworld] [seed=1337] [pregen=5000]");
			return true;
		}

		String worldName = args[0];
		String type = "overworld";
		long seed = 1337;
		int pregen = 0;
		boolean multiverse = Iris.linkMultiverseCore.supported();

		for(String i : args)
		{
			type = i.startsWith("type=") ? i.split("\\Q=\\E")[1] : type;
			seed = i.startsWith("seed=") ? Long.valueOf(i.split("\\Q=\\E")[1]) : seed;
			pregen = i.startsWith("pregen=") ? Integer.parseInt(i.split("\\Q=\\E")[1]) : pregen;
		}

		Iris.linkMultiverseCore.assignWorldType(worldName, type);
		World world = null;
		IrisDimension dim;
		File folder = new File(worldName);
		if(multiverse)
		{
			dim = IrisDataManager.loadAnyDimension(type);
			String command = "mv create " + worldName + " " + Iris.linkMultiverseCore.envName(dim.getEnvironment());
			command += " -s " + seed;
			command += " -g Iris";
			sender.sendMessage("Delegating " + command);
			Bukkit.dispatchCommand(sender, command);
			world= Bukkit.getWorld(worldName);
		}

		else
		{
			if(folder.exists())
			{
				sender.sendMessage("That world folder already exists!");
				return true;
			}

			File iris = new File(folder, "iris");
			iris.mkdirs();

			dim = Iris.proj.installIntoWorld(sender, type, folder);

			WorldCreator wc = new IrisWorldCreator().dimension(dim).name(worldName)
					.productionMode().seed(seed).create();
			sender.sendMessage("Generating with " + Iris.getThreadCount() + " threads per chunk");
			O<Boolean> done = new O<Boolean>();
			done.set(false);

			J.a(() ->
			{
				double last = 0;
				int req = 800;
				while(!done.get())
				{
					boolean derp = false;
					double v = (double) ((IrisAccess) wc.generator()).getGenerated() / (double) req;

					if(last > v || v > 1)
					{
						derp = true;
						v = last;
					}

					else
					{
						last = v;
					}

					sender.sendMessage("Generating " + Form.pc(v) + (derp ? " (Waiting on Server...)" : ""));
					J.sleep(3000);
				}
			});

			world = INMS.get().createWorld(wc, false);

			done.set(true);
		}


		sender.sendMessage(worldName + " Spawn Area generated.");

		O<Boolean> b = new O<Boolean>();
		b.set(true);

		if(pregen > 0)
		{
			b.set(false);
			sender.sendMessage("Pregenerating " + worldName + " " + pregen + " x " + pregen);
			sender.sendMessage("Expect server lag during this time. Use '/iris pregen stop' to cancel");

			new Pregenerator(world, pregen, () ->
			{
				b.set(true);
			});
		}

		IrisDimension dimm = dim;
		long seedd = seed;
		World ww = world;
		J.a(() ->
		{
			while(!b.get())
			{
				J.sleep(1000);
			}


			Bukkit.getScheduler().scheduleSyncDelayedTask(Iris.instance, () ->
			{
				ww.save();
				sender.sendMessage("All Done!");
			});
		});

		return true;
	}

	@Override
	protected String getArgsUsage()
	{
		return "<name> [type=overworld] [seed=1337] [pregen=5000]";
	}
}
