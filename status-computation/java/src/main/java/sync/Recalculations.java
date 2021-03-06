package sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class Recalculations {

	public ArrayList<RecalcItem> list;
	
	private class RecalcItem
	{
		String reason;		//Recalculation reason
		String status;      //Recalculation status
		String supergroup;	//Name of supergroup that adheres to recalculation
		String start; 		//Start timestamp
		String end;			//Start timestamp
		String submitted;
		ArrayList<String> exclude; //Exclude list
		
		public RecalcItem(){
			// Initializations
			this.reason="";
			this.status="";
			this.supergroup="";
			this.start=""; 
			this.end="";
			this.submitted="";
			this.exclude = new ArrayList<String>();
		}
		
		public RecalcItem(String reason, String status, String supergroup, String start, String end, String submitted, ArrayList<String> exclude){
			this.reason=reason;
			this.status=status;
			this.supergroup=supergroup;
			this.start=start; 
			this.end=end;
			this.submitted=submitted;
			this.exclude = exclude;
		}
	}
	
	public Recalculations(){
		this.list = new ArrayList<RecalcItem>();
	}
	
	// Clear all the recalc data
	public void clear(){
		this.list = new ArrayList<RecalcItem>();
	}
	
	public void insert(String reason, String status, String supergroup, String start, String end, String submitted, ArrayList<String> exclude){
		this.list.add(new RecalcItem(reason,status,supergroup,start,end,submitted,exclude));
	}
	
	public int count(){
		return this.list.size();
	}
	
	public boolean check(String supergroup, String groupname, String targetDate) throws ParseException{
		
		for (RecalcItem item : this.list)
		{
			// supergroup found
			if (item.supergroup.equalsIgnoreCase(supergroup)){
				
				// check if site is excluded
				for (String subitem : item.exclude){
					if (groupname.equalsIgnoreCase(subitem)) return false;
				}
				
				// check dates
				SimpleDateFormat dmy = new SimpleDateFormat("yyyy-MM-dd");
				Date sDate = dmy.parse(item.start);
				Date eDate = dmy.parse(item.end);
				Date tDate = dmy.parse(targetDate);
				
				
				return (tDate.compareTo(sDate) >= 0 && tDate.compareTo(eDate) <= 0);
				
			}
		}
		
		
		return false;
	}
	
	public String getStart(String supergroup)
	{
		for (RecalcItem item: this.list)
		{
			if (item.supergroup.equalsIgnoreCase(supergroup)){
				return item.start;
			}
		}
		
		return null;
	}
	
	public String getEnd(String supergroup)
	{
		for (RecalcItem item: this.list)
		{
			if (item.supergroup.equalsIgnoreCase(supergroup)){
				return item.end;
			}
		}
		
		return null;
	}
	
	public void loadJson(File jsonFile) throws FileNotFoundException{
		
		this.clear();
		
		BufferedReader br = new BufferedReader(new FileReader(jsonFile));
		JsonParser jsonParser = new JsonParser();
		JsonElement jRootElement = jsonParser.parse(br);
		JsonArray jRootObj = jRootElement.getAsJsonArray();
		
		for (JsonElement item : jRootObj)
		{
			String reason = item.getAsJsonObject().get("r").getAsString();
			String status = item.getAsJsonObject().get("s").getAsString();
			String start = item.getAsJsonObject().get("st").getAsString();
			String end = item.getAsJsonObject().get("et").getAsString();
			String submitted = item.getAsJsonObject().get("t").getAsString();
			String supergroup = item.getAsJsonObject().get("n").getAsString();
			
			ArrayList<String> exclude = new ArrayList<String>();
			// Get the excluded
			JsonArray jExclude = item.getAsJsonObject().get("es").getAsJsonArray();
			for (JsonElement subitem: jExclude)
			{
				exclude.add(subitem.getAsString());
			}
			
			this.insert(reason, status, supergroup, start, end, submitted, exclude);
		}
	
	}
	
	
	
}
