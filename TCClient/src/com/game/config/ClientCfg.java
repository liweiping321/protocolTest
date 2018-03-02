package com.game.config;

import com.game.utils.LogUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

/**
 * @author jianpeng.zhang
 * @since 2017/2/17.
 */
@Root(name = "ClientCfg")
public class ClientCfg {
	@ElementArray(name = "Columns", entry = "Column")
	private String[] column;

	@ElementArray(name = "HideColumn", entry = "Column", required = false)
	private String[] hideColumn;

	@Element(name = "IsFixed", required = false)
	private boolean fixed;

    @Element
    private Parametric parametric;

    public String[] getColumn()
    {
        return column;
    }

	public void setColumn(String[] column) {
		this.column = column;
	}

	public String[] getHideColumn() {
		return hideColumn;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
		save();
	}

    public Parametric getParametric()
    {
        return parametric;
    }

    public void setParametric(Parametric parametric)
    {
        this.parametric = parametric;
    }

    public void setHideColumn(String[] hideColumn) {
		this.hideColumn = hideColumn;
		save();
	}

	private void save() {
		File saveFile = TCConfig.getConfigPath().toFile();
		Serializer serializer = new Persister();
		try {
			serializer.write(this, saveFile);
		} catch (Exception e) {
			LogUtils.error("保存xml config失败", e);
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		ClientCfg clientCfg = new ClientCfg();
		clientCfg.setColumn(new String[] { "dsfw", "wifj", "kmei" });

		Serializer serializer = new Persister();
		File result = new File("customer.xml");

		serializer.write(clientCfg, result);
	}
}

// @Root(name="Columns")
// class Columns {
//
// @ElementMap(entry="hide", key="Column", attribute=true, inline=true)
// private Map<String, String> map;
//
// public Map<String, String> getMap() {
// return map;
// }
//
// public void setMap(Map<String, String> map)
// {
// this.map = map;
// }
// }
