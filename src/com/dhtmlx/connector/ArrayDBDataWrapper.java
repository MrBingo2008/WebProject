package com.dhtmlx.connector;

public class ArrayDBDataWrapper extends DBDataWrapper {

	protected ConnectorResultSet data;

	public ArrayDBDataWrapper(Iterable<Object> data) {
		this.data = new ArrayConnectorResultSet(data);
	}

	@Override
	public ConnectorResultSet select(DataRequest source) throws ConnectorOperationException {
		return data;
	}

	@Override
	public String escape(String data) {
		return null;
	}

	@Override
	public String get_new_id(ConnectorResultSet result)
			throws ConnectorOperationException {
		return null;
	}

}