package com.game.bean;

import java.util.ArrayList;
import java.util.List;

public class ModuleBean {
			private String  name;
			
			 private List<ProtoMessage> msgs=new ArrayList<>();

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public List<ProtoMessage> getMsgs() {
				return msgs;
			}

			public void setMsgs(List<ProtoMessage> msgs) {
				this.msgs = msgs;
			}
			 
			 
}	
	


