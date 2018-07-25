package com.dhtmlx.connector;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONSchedulerDataItem extends SchedulerDataItem {

	public JSONSchedulerDataItem(HashMap<String, String> data, DataConfig config, int index) {
		super(data, config, index);
	}

	@SuppressWarnings("unchecked")
	public void to_json(JSONArray output) {
		if (!skip) {
			JSONObject record = new JSONObject();

			record.put("id", get_id());
			record.put("start_date", get_value(config.data.get(0).name));
			record.put("end_date", get_value(config.data.get(1).name));
			record.put("text", get_value(config.data.get(2).name));
			for (int i=3; i<config.data.size(); i++)
				record.put(config.data.get(i).name, get_value(config.data.get(i).name));

			userdata_to_json(record);
			output.add(record);
		}
	}

}
