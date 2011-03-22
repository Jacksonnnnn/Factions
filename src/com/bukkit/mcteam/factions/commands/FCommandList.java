package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.util.TextUtil;

public class FCommandList extends FBaseCommand {
	
	public FCommandList() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		optionalParameters.add("page");
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Show a list of the factions";
	}
	
	// TODO put the 0 faction at the highest position
	public void perform() {
		ArrayList<Faction> FactionList = new ArrayList<Faction>(Faction.getAll());

		int page = 1;
		if (parameters.size() > 0) {
			try {
				page = Integer.parseInt(parameters.get(0));
			} catch (NumberFormatException e) {
				// wasn't an integer
			}
		}
		page -= 1;

		// Sort by total followers first
		Collections.sort(FactionList, new Comparator<Faction>(){
			@Override
			public int compare(Faction f1, Faction f2) {
				if (f1.id == 0)
					return 1;
				else if (f2.id == 0)
					return -1;
				else if (f1.getFPlayers().size() < f2.getFPlayers().size())
					return 1;
				else if (f1.getFPlayers().size() > f2.getFPlayers().size())
					return -1;
				return 0;
			}
		});

		// Then sort by how many members are online now
		Collections.sort(FactionList, new Comparator<Faction>(){
			@Override
			public int compare(Faction f1, Faction f2) {
				if (f1.getFPlayersWhereOnline(true).size() < f2.getFPlayersWhereOnline(true).size())
					return 1;
				else if (f1.getFPlayersWhereOnline(true).size() > f2.getFPlayersWhereOnline(true).size())
					return -1;
				return 0;
			}
		});

		int maxPage = (int)Math.floor((double)FactionList.size() / 9D);
		if (page < 0 || page > maxPage) {
			sendMessage("The faction list is only " + (maxPage+1) + " page(s) long");
			return;
		}

		String header = "Faction List";
		if (maxPage > 1) header += " (page " + (page+1) + " of " + (maxPage+1) + ")";
		sendMessage(TextUtil.titleize(header));

		int maxPos = (page+1) * 9;
		if (maxPos > FactionList.size()) maxPos = FactionList.size();
		for (int pos = page * 9; pos < maxPos; pos++) {
			Faction faction = FactionList.get(pos);
			if (faction.id == 0) {
				sendMessage(faction.getTag(me)+Conf.colorSystem+" "+faction.getFPlayersWhereOnline(true).size() + " online");
			} else {
				sendMessage(faction.getTag(me)+Conf.colorSystem+" "+faction.getFPlayersWhereOnline(true).size()+"/"+faction.getFPlayers().size()+" online, "+faction.getLandRounded()+"/"+faction.getPowerRounded()+"/"+faction.getPowerMaxRounded());
			}
		}
	}
	
}
