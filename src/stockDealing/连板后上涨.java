package stockDealing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class 连板后上涨 {

	//399006创业板指； 399102创业板综； 999999上证综指； 399001深证成指； 399106深证综指
	//param数组对应字段
	static int 基金代码 = 0;
	static int 基金名称 = 1;
	static int 持仓股数 = 2;
	static int 占本基金所持流通股比例 = 3;
	static int 持仓变化万股 = 4;
	static int 持仓市值 = 5;
	static int 占净值百分比 = 6;
	static int 占个股流通市值比例 = 7;

	//对基金持股数据进行处理
	public void dealFundHolding(String txt) throws IOException {
		//读入数据
		HashMap<String, HashMap<String, String[]>> fund = new HashMap<>();
		File file = new File(txt);
		FileReader reader = new FileReader(file);
		BufferedReader bReader = new BufferedReader(reader);
		String s = "";
		String num = "";
		int count = 0;
		int i = 0;
		HashMap<String, String[]> param = new HashMap<>();
		while ((s = bReader.readLine()) != null) {
			if (s.length() <= 12 && s.length() >= 9) {
				if (!num.equals("")) {
					fund.put(num, param);
				}
				num = s;
				i = 0;
			} else if (s.length() < 6) {
				param = new HashMap<>();
				count = Integer.valueOf(s);
			} else if (s.length() > 15) {
				if (i < count) {
					String[] split = s.split("\t");
					param.put(split[基金名称], split);
					i++;
				}
			}
		}
		if (!num.equals("")) {
			fund.put(num, param);
		}
		bReader.close();

		System.out.println("股票代码，股票名称，基金名称，流通盘比例");
		//首先按照前两个字符进行分组。基金名称完全相同的合并。找出只有一家首两字符相同的股
		for (Map.Entry<String, HashMap<String, String[]>> entry : fund.entrySet()) {
			String key = entry.getKey();
			HashMap<String, String[]> value = entry.getValue();
			HashMap<String, HashSet<String[]>> result = new HashMap<>();

			for (String keyStr : value.keySet()) {
				boolean flag = true;
				if (result.get(key) == null) {
					result.put(key, new HashSet<>());
				} else {
					String firstTwo = keyStr.substring(0, 2);
					for (String[] singleFund : result.get(key)) {
						if (singleFund[1].contains(firstTwo)) {
							flag = false;
						}
					}
				}
				if (flag) {
					result.get(key).add(value.get(keyStr));
				}
			}


			if (result.get(key) != null && result.get(key).size() <= 1) {
				System.out.println(key);

				for (String[] strings : value.values()) {
					System.out.println(strings[1] + strings[7]);
				}
			}
		}
	}

}
