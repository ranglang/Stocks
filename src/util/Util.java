package util;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

	//399006创业板指； 399102创业板综； 999999上证综指； 399001深证成指； 399106深证综指
	int 日期 = 0;
	int 开盘价 = 1;
	int 最高价 = 2;
	int 最低价 = 3;
	int 收盘价 = 4;
	int 成交量 = 5;
	int 成交额 = 6;

	//获取沪深两市股票代码和股票名称
	public List getStockNum() throws IOException {
		List<String[]> stockNums = new ArrayList<>();
		File file = new File("E:\\GTJA\\RichEZ\\T0002\\export");
		File[] array = file.listFiles();
		for (int i = 0; i < array.length; i++) {
			String stockNum = array[i].getName();
			File singleFile = new File("E:\\GTJA\\RichEZ\\T0002\\export\\" + stockNum);
			InputStreamReader isr = new InputStreamReader(new FileInputStream(singleFile), "GBK");
			BufferedReader read = new BufferedReader(isr);
			String[] title = read.readLine().split(" ");
			stockNums.add(new String[]{title[0], title[1]});
		}
		return stockNums;
	}

	//获取沪深两市单只股票历史开盘价，收盘价，最高价，最低价，成交量
	public HashMap<String, List<String[]>> getSingleStock(String stockNum) throws IOException {
		String head;
		if(stockNum.indexOf("60")==0 || stockNum.indexOf("99")==0){
			head = "SH#";
		}else {
			head = "SZ#";
		}
		String fileName = head + stockNum + ".txt";
		File singleFile = new File("E:\\GTJA\\RichEZ\\T0002\\export\\" + fileName);
		InputStreamReader isr = new InputStreamReader(new FileInputStream(singleFile), "GBK");
		BufferedReader read = new BufferedReader(isr);
		String[] temp = read.readLine().split(" ");
		String title = temp[0] + temp[1];
		List<String[]> detail = new ArrayList<>();
		read.readLine();
		String str;
		HashMap<String, List<String[]>> singleStock = new HashMap<>();
		while ((str = read.readLine()) != null) {
			temp = str.split("\t");
			detail.add(temp);
		}
		singleStock.put(title, detail);

		return singleStock;
	}

	//计算单个股票的N日均线，成交量等
	public HashMap<String, HashMap<Date, Double>> averageParam(HashMap<String, List<String[]>> single, int param, int n) throws ParseException {
		List<String[]> days = (List<String[]>) new ArrayList(single.values()).get(0);
		HashMap<String, HashMap<Date, Double>> stockAverage = new HashMap<>();
		HashMap<Date, Double> ma = new HashMap<>();
		for (int i = 0; i < days.size(); i++) {
			String[] day = days.get(i);
			if (day[0].contains("通达信")) {
				continue;
			}
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = simpleDateFormat.parse(day[日期]);
			if (i < n - 1) {
				ma.put(date, null);
				continue;
			}
			double maToday = 0.0;
			for (int j = 0; j < n; j++) {
				String[] temp = days.get(i - j);
				maToday += Double.valueOf(temp[param]);
			}
			ma.put(date, maToday / n);
		}
		stockAverage.put(single.keySet().toString(), ma);
		return stockAverage;
	}

}
