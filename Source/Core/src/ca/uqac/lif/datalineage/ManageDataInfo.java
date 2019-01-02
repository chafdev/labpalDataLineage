package ca.uqac.lif.datalineage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ca.uqac.lif.datalineage.DataInfo.TYPEOBJ;

public class ManageDataInfo {

	/** Constructeur privé */
	private ManageDataInfo() {
	}

	/** Instance unique pré-initialisée */
	private static ManageDataInfo INSTANCE = new ManageDataInfo();

	/** Point d'accès pour l'instance unique du singleton */
	public static ManageDataInfo getInstance() {
		return INSTANCE;
	}

	List<DataInfo> lst = new ArrayList<>();

	public void sort() {

		Comparator<DataInfo> compareByName = Comparator.comparing(DataInfo::getId).thenComparing(DataInfo::getTime);
		Collections.sort(lst, compareByName);

	}

	public List<DataInfo> getList(TYPEOBJ type) {
		List<DataInfo> lstE = new ArrayList<>();
		for (DataInfo dt : lst) {

			if (dt.type == type) {
				lstE.add(dt);
			}
		}
		return lstE;
	}

	StringBuilder start() {
		StringBuilder rowshtmlTable = new StringBuilder();
		StringBuilder tagTable = new StringBuilder(
				"<button id=\"btn1aspectj\" onclick=\"myFunction('yes')\">Display Lineage</button>\r\n"
						+ "<button id=\"btn2aspectj\" class=\"hidden\"  onclick=\"myFunction('no')\">Hide</button>\r\n"
						+ "<div id=\"aspectj\" class=\"hidden\">" + "<table class=\"winner-table\">\r\n" + "\r\n"
						+ "<tr>\r\n" + "\r\n" + "<th class=\"city-table\">#</th>\r\n"
						+ "<th class=\"city-table\">Type</th>\r\n" + "<th class=\"city-table\">Time</th>\r\n"
						+ "<th class=\"city-table\">Id</th>\r\n" + "<th class=\"city-url\">Description</th>\r\n"
						+ "<th class=\"city-table\">Line</th>\r\n" + "<th class=\"city-url\">Content</th>\r\n"
						+ "</tr>\r\n"

						+ "\r\n" + "");
		rowshtmlTable.append(tagTable);
		return rowshtmlTable;
	}

	StringBuilder end(StringBuilder rowshtmlTable, StringBuilder sb) {
		rowshtmlTable.append(sb);
		rowshtmlTable.append("</table></div>");
		return rowshtmlTable;
	}

	public String getTransformation(String id, TYPEOBJ to) {
		DataInfo.count = 0;
		StringBuilder rowshtmlTable = start();
		StringBuilder sb = new StringBuilder();

		List<DataInfo> lstE = getList(to);
		for (DataInfo dt : lstE) {
			if (dt.getId().equals(id) || dt.getIdInstance().equals(id)) {
				sb.append(dt.toHtml());
			}
		}

		return end(rowshtmlTable, sb).toString();

	}

	public void createTree() {
		StringBuilder st = new StringBuilder();
		Node<DataInfo> node = addNode();
		printTree(node, " ", st);
	}

	public void addDataInfo(DataInfo dt) {
		lst.add(dt);
	}

	public boolean isExist(Node<DataInfo> root, DataInfo dt) {

		List<Node<DataInfo>> childs = root.getChildren();
		boolean b = false;
		for (Node<DataInfo> dts : childs) {
			DataInfo di = dts.getData();
			if (dt.id.equals(di.getId())) {
				b = true;
			}
		}
		return b;
	}

	private <T> Node<DataInfo> addNode() {

		Node<DataInfo> nodec = null;
		Node<DataInfo> root = new Node<>(new DataInfo());
		List<DataInfo> lstE = getList(TYPEOBJ.EXP);
		int i = 0;

		for (DataInfo dt : lstE) {
			i++;
			if (i < 100) {
				if (isExist(root, dt)) {
					if (nodec == null) {
						nodec = root.addChild(new Node<DataInfo>(dt));
					} else {
						nodec = nodec.addChild(new Node<DataInfo>(dt));
					}
				} else {
					nodec = root.addChild(new Node<DataInfo>(dt));
				}
			}
		}
		return root;
	}

	private <T> void printTree(Node<T> node, String appender, StringBuilder st) {
		// creating JSONObject

		StringBuilder l = st.append(new StringBuilder(appender))
				.append(new StringBuilder(((DataInfo) node.getData()).toString()));
		node.getChildren().forEach(each -> printTree(each, " ", l));

	}

	private <T> void printJSON(Node<T> node, String appender, JSONArray jsArrayChildren) {
		// creating JSONObject
		DataInfo dt = (DataInfo) node.getData();
		JSONObject jNode = new JSONObject();
		jNode.put("name", dt.getName());
		jNode.put("id", dt.getId());
		// jNode.put("state", dt.getTime().toString());

		jsArrayChildren.add(jNode);

		JSONArray jArrayChild = new JSONArray();

		for (Node<T> nd : node.getChildren()) {
			DataInfo dtn = (DataInfo) nd.getData();
			JSONObject jNodeChild = new JSONObject();
			if (nd.getChildren().size() > 0) {
				jNodeChild.put("children", jArrayChild);
				printJSON(nd, " ", jArrayChild);
			} else {
				jNodeChild.put("name", dtn.getName());
				jNodeChild.put("id", dtn.getId());
				// jNodeChild.put("state", dtn.getTime().toString());
			}
			jArrayChild.add(jNodeChild);
		}
		jNode.put("children", jArrayChild);

	}
}
