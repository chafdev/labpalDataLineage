package ca.uqac.lif.datalineage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.table.Table;

public class DataInfo implements Comparable<DataInfo> {
	public enum TYPEOBJ {
		EXP, TABLE, TABLE_TRANSFOMATION, TRANSFORMATION, NULL
	}

	String id = "-1";
	String idJoint = "-1";
	String idInstance = "-1";
	Object tinstance = null;
	String name = "";
	String source;
	String dest;
	String line;
	String content;
	TYPEOBJ type = TYPEOBJ.NULL;
	Timestamp time;
	boolean construct = false;

	public DataInfo(Object tinstance, String source, String destination, String line, boolean construct) {
		super();
		this.construct = construct;
		this.tinstance = tinstance;
		this.source = source;
		this.dest = destination;
		this.time = new Timestamp(System.currentTimeMillis());
		this.name = tinstance.getClass().getSimpleName();
		this.line = line;
		this.setType();
		this.setId();

	}

	public DataInfo(String source, String dest, String line) {

		this.source = source;
		this.dest = dest;
		this.line = line;
		this.time = new Timestamp(System.currentTimeMillis());
		this.construct = true;
	}

	public DataInfo() {

		this.construct = true;
		this.tinstance = null;
		this.source = "origine";
		this.dest = "0";
		this.time = new Timestamp(System.currentTimeMillis());
		this.name = "origin";
	}

	// Experiment
	List<Dependency<?>> lstDeps = new ArrayList<>();

	@Override
	public int compareTo(DataInfo o) {

		return o.time.compareTo(this.time);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId() {
		if (tinstance instanceof Table) {
			Table e = (Table) tinstance;
			if (e.data_id.equals("") || construct) {
				setId(UUID.randomUUID().toString());
				e.data_id = getId();
			} else {
				setId(e.data_id);
			}
			setIdInstance(Integer.toString(e.getId()));
			if(e.getDataTable()!=null)
			setContent(e.getDataTable().toString());

		} else if (tinstance instanceof Experiment) {
			Experiment e = (Experiment) tinstance;

			if (e.data_id.equals("") || construct) {
				setId(UUID.randomUUID().toString());
				e.data_id = getId();
			} else {
				setId(e.data_id);
			}
			setIdInstance(Integer.toString(e.getId()));
			setContent(e.getAllParameters().toString());
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setContent() {
		if (getTinstance() != null) {
			if (tinstance instanceof Table) {
				type = TYPEOBJ.TABLE;

			} else if (tinstance instanceof Experiment) {
				type = TYPEOBJ.EXP;

			}
		}
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getDestination() {
		return dest;
	}

	public void setDestination(String destination) {
		this.dest = destination;
	}

	public TYPEOBJ getType() {
		return type;
	}

	public void setType(TYPEOBJ type) {
		this.type = type;
	}

	public String getIdJoint() {
		return idJoint;
	}

	public void setIdJoint(String idJoint) {
		this.idJoint = idJoint;
	}

	public void setType() {

		if (getTinstance() != null) {
			if (tinstance instanceof Table) {
				setType(TYPEOBJ.TABLE);

			} else if (tinstance instanceof Experiment) {
				setType(TYPEOBJ.EXP);

			}
		}
	}

	public Object getTinstance() {
		return tinstance;
	}

	public void setTinstance(Object tinstance) {
		this.tinstance = tinstance;
	}

	public String getIdInstance() {
		return idInstance;
	}

	public void setIdInstance(String idInstance) {
		this.idInstance = idInstance;
	}

	@Override
	public String toString() {

		return "DataInfo [id=" + id + ", name=" + name + ", source=" + source + ", dest=" + dest + ", line=" + line
				+ ", content=" + content + ", type=" + type + ", time=" + time + ", construct=" + construct
				+ ",idjoint= " + idJoint + "]" + "\n";
	}

	static int count = 0;

	public StringBuilder toHtml() {

		StringBuilder row = new StringBuilder();
		row.append("  <tr>");

		row.append("   <td class=\"city-table\">" + count + "</td>");
		row.append("   <td class=\"city-table\">" + this.getName() + "</td>");
		row.append("   <td class=\"city-table\">" + this.getTime() + "</td>");
		row.append(
				"   <td class=\"city-table\">" + "<a href=\"/recipients.html\">" + getIdInstance() + "</a>" + "</td>");
		row.append("   <td class=\"city-url\">" + " From  :" + this.getSource() + ", To : " + this.getDestination()
				+ "</td>");
		row.append("   <td class=\"city-table\">" + line + "</a>" + "</td>");
		row.append("   <td class=\"city-url\">" + this.getContent() + "</td>");
		row.append("  </tr>");
		count++;

		return row;
	}

}