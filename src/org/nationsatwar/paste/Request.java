package org.nationsatwar.paste;

import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONObject;

public class Request {
	public int Id;
	public Date Time;
	public RequestType Type;
	public RequestStatus Status;
	public String User;
	public JSONObject Data;
	
	public Request(int ID, RequestType Type, RequestStatus Status, String User, JSONObject Data) {
		this.Id = ID;
		this.Time = Calendar.getInstance().getTime();
		this.Type = Type;
		this.Status = Status;
		this.User = User;
		this.Data = Data;
	}
	
	public enum RequestType {
		SETTOKEN, CONFIRMTOKEN
	}
	
	public enum RequestStatus {
		UNSENT, SENT, COMPLETED, ERROR, REJECTED
	}
	
	/*public Request() {
		
	}*/
}
