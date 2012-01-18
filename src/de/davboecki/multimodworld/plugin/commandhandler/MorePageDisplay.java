package de.davboecki.multimodworld.plugin.commandhandler;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MorePageDisplay {

	private ArrayList<String> header = new ArrayList<String>();
	private ArrayList<StringConnected> content = new ArrayList<StringConnected>();
	
	private final int zeilen = 20;
	private final int zeichen = 50;

	private int currentpage = 0;
	private int currentpagecount = 0;
	private boolean terminated=false;

	MorePageDisplay(ArrayList header,String name){
		if(header.size() <= (zeilen - 2)){
			this.header = header;			
		}
		ConfirmListener.register(this,name);
	}

	MorePageDisplay(String[] header,String name){
		if(header.length <= (zeilen - 2)){
			this.header = new ArrayList();
			for(int i=0;i<header.length;i++){
				this.header.add(header[i]);
			}
		}
		ConfirmListener.register(this,name);
	}

	public int getZeilen(){
		return zeilen;
	}

	public int getZeichen(){
		return zeichen;
	}
	
	public void  append(String input) {
		append(input,false);
	}
		
	public void  append(String input,boolean flag) {
		content.add(new StringConnected(input,flag));
	}

	public boolean isTerminated(){
		return terminated;
	}
	
	public void display(CommandSender sender) {
		display(sender,1);
	}

	public void display(Player player){
		display(player,1);
	}
	public void display(Player player,int page){
		display((CommandSender)player,page);
	}
	
	private String ReplaceMeta(String input,int page,int count) {
		String output = "";
		int pagecount = (int) Math.ceil(((double)content.size()) / ((double)count));
		if(count == -1) pagecount = 0;
		if(count == -2) pagecount = 1;
		for(int i=0;i<input.length();i++) {
			char c = input.charAt(i);
			switch(c){
			case '%':
				output += String.valueOf(page);
				break;
			case '$':
				output += String.valueOf(pagecount);
				break;
			default:
				output += c;
				break;
			}
		}
		boolean LeftDone=false;
		boolean RightDone=false;
		String output2 = "";
		//Add = for <>
		for(int i=0;i<output.length();i++) {
			char c = output.charAt(i);
			switch(c){
			case '<':
				if(LeftDone)break;
				LeftDone = true;
				output2 += ChatColor.AQUA;
				for(int j=0;j<((zeichen-output.length())/2);j++){
					output2 += "=";
				}
				output2 += ChatColor.WHITE;
				break;
			case '>':
				if(RightDone)break;
				RightDone = true;
				output2 += ChatColor.AQUA;
				for(int j=0;j<((zeichen-output.length())/2);j++){
					output2 += "=";
				}
				output2 += ChatColor.WHITE;
				break;
			case '(':
				if(LeftDone)break;
				LeftDone = true;
				for(int j=0;j<((zeichen-output.length())/2);j++){
					output2 += " ";
				}
				break;
			case ')':
				if(RightDone)break;
				RightDone = true;
				for(int j=0;j<((zeichen-output.length())/2);j++){
					output2 += " ";
				}
				break;
			default:
				output2 += c;
				break;
			}
		}
		return output2;
	}
	 // 

	public boolean HandleChat(String input,CommandSender sender) {
		if(terminated) return false;
		if(input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")){
			terminated = true;
			for(Object zeilenobject:header.toArray()) {
				if(!(zeilenobject instanceof String))continue;
				sender.sendMessage(ReplaceMeta((String)zeilenobject,0,-1));
			}
			clearscreen(sender,19-header.size());
			sender.sendMessage(ChatColor.AQUA+"Pageview: "+ChatColor.RED+"Exit.");
		} else if(input.equalsIgnoreCase("next") || input.equalsIgnoreCase("nex") || input.equalsIgnoreCase("n")){
			if(currentpage > (currentpagecount-1)){
				display(sender,0);
			} else {
				currentpage++;
				display(sender,currentpage);
			}
		} else if(input.equalsIgnoreCase("previous") || input.equalsIgnoreCase("pre") || input.equalsIgnoreCase("p")){
			if(currentpage < 2){
				display(sender,currentpagecount);
			} else {
				currentpage--;
				display(sender,currentpage);
			}
		} else if(CommandHandler.isNumber(input)) {
			if(CommandHandler.toNumber(input) <= currentpagecount && CommandHandler.toNumber(input) > 0){
				display(sender,CommandHandler.toNumber(input));
			} else {
				display(sender,currentpage,true);
				sender.sendMessage(ChatColor.AQUA+"Pageview:"+ChatColor.RED+" Not a valid number.");
			}
		} else if(input.equalsIgnoreCase("reprint")) {
			display(sender,currentpage);
		} else if(input.equalsIgnoreCase("all")) {
			display(sender,currentpage,false,true);
		} else {
			//display(sender,currentpage,true);
			printLastLine(sender,true);
		}
		return true;
	}
	
	private void clearscreen(CommandSender sender,int count){
		for(int i=0;i<count;i++){
			sender.sendMessage("|");
		}
	}
	
	private int countnextcontent(int start){
		int  i;
		for(i=start;i<(content.size()-1) && content.get(i+1).connected;i++);
		return i - start;
	}
	
	public void display(CommandSender sender,int page) {
		display(sender,page,false,false);
	}
	public void display(CommandSender sender,int page,boolean flag) {
		display(sender,page,flag,false);
	}
	
	public void printLastLine(CommandSender sender){
		printLastLine(sender,false);
	}
	
	public void printLastLine(CommandSender sender,boolean flag){
		sender.sendMessage(ChatColor.AQUA+"Pageview:"+ChatColor.WHITE+" Enter "+ChatColor.RED+"Pre"+ChatColor.WHITE+"/"+ChatColor.GREEN+"Next"+ChatColor.WHITE+", a "+ChatColor.AQUA+"number"+ChatColor.WHITE+", "+ChatColor.AQUA+"all"+ChatColor.WHITE+", "+ChatColor.AQUA+"reprint"+ChatColor.WHITE+" or "+ChatColor.RED+"exit"+ChatColor.WHITE+(flag?"!":"."));
	}
	
	public void display(CommandSender sender,int page,boolean flag,boolean all) {
		if(terminated) return;
		int count = zeilen - header.size() - 1;
		page = (page>0&&!all?page:1);
		currentpage = page;
		int pagecount = (int) Math.ceil(((double)content.size()) / ((double)count));
		currentpagecount = pagecount;
		if(all) count = -2;
		for(Object zeilenobject:header.toArray()) {
			if(!(zeilenobject instanceof String))continue;
			sender.sendMessage(ReplaceMeta((String)zeilenobject,page,count));
		}
		int i;
		for(i=count*(page-1);i<content.size()&&(i<(count*page)||all);i++) {
			if(!all) {
				int nexttake = countnextcontent(i);
				if((nexttake+i)>=(count*page)) {
					break;
				}
			}
			sender.sendMessage(content.get(i).content);
		}
		if(!all) clearscreen(sender,(count*page)-i);
		if(!flag) printLastLine(sender);
	}
	
	private class StringConnected{
		StringConnected(String s,boolean b){
			content = s;
			connected = b;
		}
		public String content;
		public boolean connected;
	}
}
